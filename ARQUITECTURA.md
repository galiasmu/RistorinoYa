# Arquitectura — Backend Ristorino

> Backend del portal Ristorino (Spring Boot). Toma como guía a **indecWS** (`../../servicios/indecWS`):
> mismas capas, mismos paquetes y mismo `SimpleJdbcCallFactory`. La integración con los restaurantes se resolvió más simple: una factory que elige la facade REST o SOAP, sin Strategy.
>
> Para el detalle de cada clase, endpoint y stored procedure, y cómo usarlo, ver **[GUIA.md](GUIA.md)**.

---

## 1. Visión general

Ristorino es el **hub de integración** entre el frontend Angular y los sistemas de los 4 restaurantes:

```
Frontend Angular (4200) ──►  Ristorino (8080) ──►  SQL Server (BD ristorino)
                                              ──►  La Bella Pizza    (REST :8081)
                                              ──►  Perukai           (SOAP :8082)
                                              ──►  La Fábrica Burger (REST :8083)
                                              ──►  Sabores del Norte (SOAP :8084)
                                              ──►  Motor de IA       (API externa, pendiente)
```

Es el mismo esquema que indecWS: Angular → IndecWS → supermercados REST/SOAP.

---

## 2. Stack tecnológico

Es igual que indecWS (mismo `pom.xml`). Solo se agrega `spring-security-crypto`, para guardar las contraseñas con BCrypt.

| Capa | Tecnología |
|---|---|
| Lenguaje / Framework | Java 17 · Spring Boot 3.5.5 (igual que los ejemplos de la cátedra) |
| REST server | Spring Web MVC |
| REST client | `Httpful` de la cátedra (guía M2-2-5): Jersey 3.0.2 + jersey-hk2 + Gson, con token Bearer |
| SOAP client | `SOAPClient` de la cátedra (guía M2-2-6): JAX-WS `Dispatch<SOAPMessage>` + JAXB + WS-Security |
| Acceso a datos | Spring Data JDBC + **SimpleJdbcCall** (solo stored procedures) |
| Base de datos | SQL Server 2019+ |
| Boilerplate / AOP | Lombok · Spring AOP |
| Login | JWT (HS256) validado con Spring Security `oauth2-resource-server` + `NimbusJwtDecoder` (guía M2-2-4) + BCrypt |

---

## 3. Estructura de directorios

```
RistorinoYa-final/
├── pom.xml
├── Doc/scripts/
│   ├── final-db-creation.sql      ← tablas (RISTORINO-Logico.pdf) + datos iniciales
│   └── procedimientos.sql         ← stored procedures
└── src/main/
    ├── java/ar/edu/ubp/das/ristorino/
    │   ├── RistorinoApplication.java     ← punto de entrada
    │   ├── controllers/                  ← endpoints REST (/api/v1/...)
    │   ├── services/                     ← interfaces de servicio
    │   │   └── impl/                     ← implementaciones
    │   ├── repository/                   ← acceso a datos (stored procedures)
    │   ├── model/                        ← integraciones externas
    │   │   ├── RestauranteConfig         ← config abstracta (REST | SOAP)
    │   │   ├── RestauranteFacade         ← operaciones que se consumen de cada restaurante
    │   │   ├── rest/  (+ client/)        ← facade y cliente REST
    │   │   ├── soap/  (+ client/)        ← facade y cliente SOAP
    │   │   ├── restauranteConfig/        ← RestConfig, SoapConfig
    │   │   ├── mappers/                  ← CustomRowMapper (config polimórfica)
    │   │   └── ia/                       ← MotorIAFacade (IA generativa, pendiente)
    │   ├── beans/                        ← objetos del portal (Lombok @Data)
    │   │   └── restaurante/              ← objetos que viajan a/desde los restaurantes (JAXB + Gson)
    │   ├── components/                   ← factories y utilidades Spring
    │   ├── config/                       ← CORS (WebConfig) + SecurityConfig (JWT)
    │   ├── aspects/                      ← AuditAspect (AOP)
    │   ├── batch/                        ← ejecutores de sincronización
    │   └── utils/                        ← Constantes, IdiomaUtils
    └── resources/application.properties
```

---

## 4. Arquitectura en capas

```
┌──────────────────────────────────────────────────────────────────────┐
│                           Controllers                                │
│ Auth · Cliente · Restaurante · Busqueda · Contenido · Reserva ·      │
│ Catalogo · AdminTask                                                 │
└────────────────────────────────┬─────────────────────────────────────┘
                                 │
┌────────────────────────────────▼─────────────────────────────────────┐
│                             Services                                 │
│ AuthService · RestauranteService · BusquedaService ·                 │
│ ContenidoService · ReservaService · CatalogoService                  │
└──────────┬───────────────────────────────────────┬───────────────────┘
           │                                       │
┌──────────▼──────────┐              ┌─────────────▼──────────────────┐
│    Repositories     │              │  Facades / Clientes externos   │
│ (stored procedures) │              │  Facades REST / SOAP + IA      │
└──────────┬──────────┘              └─────────────┬──────────────────┘
           │                                       │
┌──────────▼──────────┐              ┌─────────────▼──────────────────┐
│ SQL Server (ristorino)│             │ Servicios de los restaurantes  │
└─────────────────────┘              └────────────────────────────────┘
```

**Hay tres tipos de flujo:**
- **Lectura:** Controller → Service → Repository → SP → BD.
  Ejemplos: contenidos vigentes, sucursales, mis reservas.
- **Tiempo real con el restaurante:** Controller → Service → `RestauranteFacadeFactory` → Facade (REST o SOAP) → servicio del restaurante.
  Ejemplos: disponibilidad y confirmación de reserva.
- **Sincronización:** Batch o AdminTask → Service → Facade → servicio del restaurante; con lo que devuelve, el Service → Repository → SP → BD del portal.
  Ejemplos: sucursales, contenidos y notificación de clics.

---

## 5. Patrones de diseño

| Patrón | En Ristorino | En indecWS |
|---|---|---|
| **Facade** | `RestauranteFacade` → `RestauranteRestFacadeImpl` / `RestauranteSoapFacadeImpl` | `SupermercadoFacade` |
| **Factory** | `RestauranteFacadeFactory` (elige REST o SOAP con un `if`), `RestClientFactory`, `SOAPClientFactory`, `SimpleJdbcCallFactory` | `SuperFacadeFactory` usa Strategy |
| **Builder** | `SOAPClient.SOAPClientBuilder` | ídem |
| **AOP** | `AuditAspect` audita `RestauranteFacadeFactory` | audita `SuperFacadeFactory` |
| **RowMapper polimórfico** | `CustomRowMapper` → `RestConfig` / `SoapConfig` | ídem |

**Cómo se elige REST o SOAP:** lo que cambia de un restaurante a otro es *cómo se habla con él* (REST o SOAP, URL, credenciales). Lo que no cambia es *qué se le pide* (sucursales, disponibilidad, reserva, contenidos, clics) ni *qué hace el portal con la respuesta*.
1. `get_configuracion_restaurante` lee `configuracion_restaurantes` y `CustomRowMapper` arma un `RestConfig` o un `SoapConfig` según `tipo_servicio`.
2. `RestauranteFacadeFactory.createFacade` mira de qué tipo es esa configuración y crea `RestauranteRestFacadeImpl` o `RestauranteSoapFacadeImpl`.
3. El servicio usa la facade sin saber cuál es: `restauranteService.obtenerFacade(nro).registrarReserva(...)`.

Sumar un restaurante REST o SOAP es solo un insert en la base. Sumar un protocolo nuevo es una facade nueva, un `XxxConfig` y un `if` más en la factory; los servicios no se tocan.

**Diferencia con indecWS:** en indecWS la facade llama a la API y además guarda en la base (`saveOrUpdate...` duplicado en REST y SOAP). Acá la facade **solo se comunica con el restaurante y devuelve los datos** (`obtenerSucursales`, `obtenerContenidos`). Los guarda el servicio, a través del repository (`RestauranteRepository.insUpdSucursal`, `ContenidoRepository.insUpdContenido`). Con eso:
- la lógica del portal está una sola vez, no repetida por protocolo;
- `model/` no accede a la base: cada capa hace una sola cosa;
- un protocolo nuevo solo tiene que implementar la comunicación.

**Facade para la IA:** `MotorIAFacade` aísla al portal del proveedor de IA, igual que `RestauranteFacade` lo aísla del protocolo de cada restaurante. Para cambiar de proveedor alcanza con otra implementación.

---

## 6. Configuración de cada restaurante

Está en la base, en `configuracion_restaurantes` + `atributos`, tal como define el modelo lógico. El SP `get_configuracion_restaurante` convierte esas filas atributo/valor en una sola fila, y `CustomRowMapper` la transforma en un `RestConfig` o un `SoapConfig`.

| Atributo | REST | SOAP |
|---|---|---|
| `TIPO_SERVICIO` | `REST` | `SOAP` |
| `URL_SERVICIO` | `http://localhost:8081/api/v1` | `http://localhost:8082/services/perukai.wsdl` |
| `TOKEN` (JWT Bearer) / `USUARIO` + `PASSWORD` (WS-Security) | token generado con la clave del restaurante | `usr_admin` / `pwd_admin` |
| `NAMESPACE`, `SERVICE_NAME`, `PORT_NAME` | — | datos del WSDL |

**Para sumar un restaurante nuevo** alcanza con cargar sus filas en estas tablas: no hay que tocar código.

---

## 7. API REST expuesta

| Método | Ruta | Req | Login |
|---|---|---|---|
| POST | `/api/v1/auth/registro` | 1 | — |
| POST | `/api/v1/auth/login` → `{ token }` | 2 | — |
| POST | `/api/v1/auth/logout` | 5 | ✔ |
| GET | `/api/v1/clientes/me` | 6 | ✔ |
| POST | `/api/v1/busqueda/filtros` | 9 | — |
| POST | `/api/v1/busqueda/lenguaje-natural` (501 hasta implementar la IA) | 10/35 | — |
| GET | `/api/v1/restaurantes`, `/{nro}`, `/{nro}/sucursales` | 11 | — |
| GET | `/api/v1/contenidos?nroRestaurante=` | 12 | — |
| GET | `/api/v1/restaurantes/{nro}/sucursales/{suc}/disponibilidad?fecha=&adultos=&menores=&codZona=` | 14/37 | — |
| POST | `/api/v1/reservas` → 201 CONFIRMADA / 409 RECHAZADA | 15/38 | ✔ |
| GET | `/api/v1/reservas?codEstado=&fechaDesde=&fechaHasta=` | 16 | ✔ |
| POST | `/api/v1/contenidos/clicks` (login opcional) | 50 | — |
| GET | `/api/v1/catalogo/preferencias`, `/localidades`, `/estados-reservas` | — | — |
| POST | `/api/admin/restaurantes/{nro}/actualizar-sucursales` | 45 | — |
| POST | `/api/admin/restaurantes/{nro}/actualizar-contenidos` | 46 | — |
| POST | `/api/admin/restaurantes/{nro}/notificar-clicks` | 50 | — |
| POST | `/api/admin/restaurantes/generar-contenidos` (501 hasta implementar la IA) | 34/48 | — |

- **Idioma:** todas las consultas leen el header `Accept-Language` (`es` / `en`), igual que el catálogo de indecWS.
- **Login:** el token se envía como `Authorization: Bearer <token>`. `SecurityConfig` (Spring Security + `NimbusJwtDecoder`, como en la guía M2-2-4) lo valida en las rutas protegidas.

---

## 8. Procesamiento batch

Como en indecWS, son clases con `main`, que levantan Spring sin el servidor web y se pueden programar desde el cron del sistema operativo:

| Clase | Qué hace | Req |
|---|---|---|
| `ActualizarSucursalesEjecutor` | Sucursales, turnos, zonas y preferencias de todos los restaurantes | 45 |
| `ActualizarContenidosEjecutor` | Contenidos a publicar de todos los restaurantes | 46 |
| `NotificarClicksEjecutor` | Informa a cada restaurante los clics pendientes (`notificado = 0`) | 50 |

---

## 9. Base de datos

Las tablas siguen **`RISTORINO-Logico.pdf`**, con los nombres tal cual (incluido `zonas_turnos_sucurales_restaurantes`).

| Grupo | Tablas |
|---|---|
| Geografía | `provincias`, `localidades` |
| Restaurantes e integración | `restaurantes`, `atributos`, `configuracion_restaurantes`, `sucursales_restaurantes`, `turnos_sucursales_restaurantes`, `zonas_sucursales_restaurantes`, `zonas_turnos_sucurales_restaurantes`, `idiomas_zonas_suc_restaurantes` |
| Preferencias | `categorias_preferencias`, `dominio_categorias_preferencias`, `preferencias_restaurantes`, `preferencias_clientes` (+ tablas `idiomas_*`) |
| Clientes y reservas | `clientes`, `reservas_restaurantes`, `estados_reservas`, `idiomas_estados`, `preferencias_reservas_restaurantes` |
| Contenidos y monetización | `contenidos_restaurantes`, `clicks_contenidos_restaurantes`, `costos` |
| i18n | `idiomas` (es, en) |

**Las sucursales no se cargan a mano:** se sincronizan desde los restaurantes con AdminTask o con batch. Los valores de tipo de cocina, especialidad, estilo y nivel de precio de cada sucursal se vinculan **por nombre** con `dominio_categorias_preferencias`. Por eso los dos catálogos usan los mismos nombres.

---

## 10. Decisiones arquitectónicas clave

| Decisión | Razón |
|---|---|
| Misma estructura que indecWS | Es la arquitectura vista en la materia: capas, Facade para REST/SOAP, SPs con `SimpleJdbcCallFactory` |
| Factory simple en vez de Strategy | Solo hay dos protocolos; un `if` sobre el tipo de configuración es más fácil de leer y de explicar que una lista de strategies |
| La facade solo se comunica; el servicio guarda | Evita duplicar la persistencia en cada protocolo y deja a `model/` sin acceso a la base |
| Solo stored procedures, sin JPA | Igual que indecWS; se eliminó la versión JPA/H2 anterior |
| Disponibilidad y reserva en tiempo real | Lo pide el caso ("tiempo real y transaccional con el restaurante") |
| Sucursales y contenidos sincronizados con batch | Se consultan muy seguido y cambian poco; el portal los lee de su propia base |
| Clics guardados primero y notificados después | Si el restaurante no responde, el clic no se pierde (`notificado = 0`) y se reintenta |
| Fee calculado por Ristorino (`costos`) y enviado en la reserva | El portal liquida; el restaurante lo guarda para auditar |
| JWT validado como en la cátedra | `oauth2-resource-server` + `NimbusJwtDecoder` (guía M2-2-4); el token se firma con la misma clave al iniciar sesión |
| `MotorIAFacade` con implementación pendiente | El armado está listo; falta elegir el proveedor e implementar los 2 métodos |
