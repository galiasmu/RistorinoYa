# Guía del backend Ristorino

Esta guía explica **para qué sirve cada parte del backend y cómo se usa**: cómo levantarlo, cómo probarlo, qué hace cada carpeta, clase, endpoint y stored procedure, y cómo hacer los cambios más comunes.

- Para la explicación de la arquitectura (capas, patrones, decisiones), ver **[ARQUITECTURA.md](ARQUITECTURA.md)**.
- Para arrancar rápido, ver **[README.md](README.md)**.

## Índice

1. [Qué hace el backend](#1-qué-hace-el-backend)
2. [Requisitos](#2-requisitos)
3. [Puesta en marcha paso a paso](#3-puesta-en-marcha-paso-a-paso)
4. [Configuración (`application.properties`)](#4-configuración-applicationproperties)
5. [Recorrido de un pedido](#5-recorrido-de-un-pedido)
6. [Para qué sirve cada carpeta y cada clase](#6-para-qué-sirve-cada-carpeta-y-cada-clase)
7. [Endpoints: para qué sirven y cómo usarlos](#7-endpoints-para-qué-sirven-y-cómo-usarlos)
8. [Colección de Postman](#8-colección-de-postman)
9. [Procesos batch](#9-procesos-batch)
10. [Base de datos y stored procedures](#10-base-de-datos-y-stored-procedures)
11. [Cómo hacer los cambios más comunes](#11-cómo-hacer-los-cambios-más-comunes)
12. [Problemas comunes](#12-problemas-comunes)
13. [Pendiente](#13-pendiente)

---

## 1. Qué hace el backend

Ristorino es el portal que junta a varios restaurantes en un solo lugar. El backend está en el medio:

```
Frontend Angular (4200) ──► Backend Ristorino (8080) ──► Base SQL Server "ristorino"
                                                     ──► La Bella Pizza    (REST, 8081)
                                                     ──► Perukai           (SOAP, 8082)
                                                     ──► La Fábrica Burger (REST, 8083)
                                                     ──► Sabores del Norte (SOAP, 8084)
```

Hace tres tipos de trabajo:

| Tipo | Qué significa | Ejemplos |
|---|---|---|
| **Responder desde su propia base** | Lo que se consulta mucho y cambia poco se guarda en la base `ristorino` | Restaurantes, sucursales, búsqueda, contenidos, mis reservas, catálogos |
| **Consultar al restaurante en el momento** | Lo que tiene que estar al día se le pregunta al restaurante en cada pedido | Disponibilidad y confirmación de reservas |
| **Sincronizar** | Copiar datos de los restaurantes a la base del portal, y avisarles lo que pasó en el portal | Sucursales, contenidos, notificación de clics |

---

## 2. Requisitos

| Qué | Versión | Para qué |
|---|---|---|
| Java (JDK) | 17 | Compilar y ejecutar |
| SQL Server | 2019 o posterior | Base `ristorino` |
| SSMS o Azure Data Studio | cualquiera | Ejecutar los scripts SQL |
| IntelliJ IDEA | cualquiera | Abrir y ejecutar el proyecto (no hace falta instalar Maven: viene `mvnw`) |
| Postman | cualquiera | Probar los endpoints |
| Los 4 servicios de `servicios-restaurantes/` | — | Sin ellos funciona todo lo que sale de la base, pero no la sincronización, la disponibilidad ni las reservas |

---

## 3. Puesta en marcha paso a paso

### Paso 1. Crear la base

En SSMS o Azure Data Studio, conectado como `sa`, ejecutá **en este orden**:

1. `Doc/scripts/final-db-creation.sql`
   - Crea la base `ristorino`, todas las tablas y los datos iniciales: idiomas, preferencias, estados de reserva, costos y los 4 restaurantes con su configuración de integración.
   - Se puede volver a ejecutar: borra todo y deja la base como al principio.
2. `Doc/scripts/procedimientos.sql`
   - Crea los stored procedures. Se puede volver a ejecutar cuando cambies alguno.

> Si usás `sqlcmd`, agregá `-f 65001` para que no se rompan los acentos.

**Cómo verificar:** `SELECT * FROM ristorino.dbo.restaurantes` tiene que devolver los 4 restaurantes.

### Paso 2. Levantar los servicios de los restaurantes

Cada uno tiene su propia base y su propio script (`<proyecto>/doc/scripts/final-db-creation.sql`). Ver `servicios-restaurantes/README.md`.

**Cómo verificar:** `http://localhost:8082/services/perukai.wsdl` muestra el WSDL de Perukai, y `GET http://localhost:8081/api/v1/restaurante` (con el header `Authorization: Bearer <token>`; el token está en la colección de Postman del restaurante) devuelve los datos de La Bella Pizza.

### Paso 3. Levantar el backend

1. En IntelliJ: **File → Open** y elegí la carpeta `RistorinoYa-final` (la que tiene el `pom.xml`). Esperá a que termine de bajar las dependencias.
2. Si tu usuario o contraseña de SQL Server no son `sa` / `Password.1234`, cambialos en `src/main/resources/application.properties`.
3. Ejecutá `RistorinoApplication` (botón ▶ al lado del `main`).

**Cómo verificar:** en la consola aparece `Tomcat started on port 8080`, y `GET http://localhost:8080/api/v1/restaurantes` devuelve los 4 restaurantes.

> La base tiene que estar levantada **antes** de arrancar el backend: igual que en indecWS, `spring-boot-starter-data-jdbc` se conecta al iniciar.

### Paso 4. Cargar los datos de los restaurantes

La base del portal arranca sin sucursales ni contenidos: se traen de cada restaurante. Para cada restaurante (`{nro}` = 1 a 4):

```
POST http://localhost:8080/api/admin/restaurantes/{nro}/actualizar-sucursales
POST http://localhost:8080/api/admin/restaurantes/{nro}/actualizar-contenidos
```

Lo más cómodo es correr la carpeta **0** de la colección de Postman (ver [sección 8](#8-colección-de-postman)). También se puede con los ejecutores de `batch/` (ver [sección 9](#9-procesos-batch)).

**Cómo verificar:** `GET /api/v1/restaurantes/2/sucursales` devuelve las sucursales de Perukai, y `GET /api/v1/contenidos` devuelve promociones.

### Paso 5. Probar

Corré la colección de Postman completa (carpetas 0 a 7). Cada request tiene un test con el código de respuesta esperado.

---

## 4. Configuración (`application.properties`)

| Propiedad | Valor | Para qué sirve |
|---|---|---|
| `spring.application.name` | `ristorino` | Nombre de la aplicación |
| `server.port` | `8080` | Puerto del backend. Los restaurantes usan 8081 a 8084 |
| `spring.datasource.url` | `jdbc:sqlserver://localhost:1433;databaseName=ristorino;encrypt=false` | Dónde está la base. Cambiá `localhost:1433` si SQL Server está en otra máquina o puerto |
| `spring.datasource.username` / `password` | `sa` / `Password.1234` | Usuario de SQL Server |
| `logging.level.org.springframework.transaction` | `DEBUG` | Igual que indecWS: muestra en consola las transacciones |
| `security.jwt.secret` | texto largo (32+ caracteres) | Clave con la que se firman los tokens de login. Si se cambia, los tokens ya emitidos dejan de valer |
| `security.jwt.expiracion-minutos` | `120` | Cuánto dura una sesión |
| `ia.api.url` / `ia.api.key` / `ia.api.modelo` | vacíos | Para el motor de IA, que todavía no está implementado |

**Lo que NO está acá:** las URLs y credenciales de los restaurantes. Están en la base, en `configuracion_restaurantes` (ver [sección 10](#10-base-de-datos-y-stored-procedures)). Así se puede sumar o cambiar un restaurante sin recompilar.

---

## 5. Recorrido de un pedido

### Ejemplo 1: confirmar una reserva (consulta en el momento)

`POST /api/v1/reservas` con `Authorization: Bearer <token>`:

1. **`SecurityConfig`** dice que `/api/v1/reservas/**` necesita sesión, así que primero pasa por el filtro JWT de Spring Security.
2. **`NimbusJwtDecoder`** valida firma, vencimiento y que no esté revocado; el `nroCliente` queda en el `sub` del JWT (`@AuthenticationPrincipal Jwt`). Si el token falta o no vale, corta con **401**.
3. **`ReservaController.confirmarReserva`** recibe el `ReservaRequest` y llama al servicio.
4. **`ReservaServiceImpl.confirmarReserva`**:
   1. busca los datos del cliente (`ClienteRepository` → SP `get_cliente`);
   2. busca el costo vigente de una reserva (`ReservaRepository` → SP `get_costo_vigente`);
   3. pide la facade del restaurante a `RestauranteService.obtenerFacade(nro)`.
5. **`RestauranteServiceImpl.obtenerFacade`** lee la configuración del restaurante (SP `get_configuracion_restaurante`; `CustomRowMapper` arma un `RestConfig` o un `SoapConfig`). **`RestauranteFacadeFactory`** crea la facade REST o SOAP según esa configuración.
6. **La facade** (`RestauranteRestFacadeImpl` o `RestauranteSoapFacadeImpl`) llama al servicio del restaurante con `Httpful` o `SOAPClient` (clases de la cátedra) y devuelve un `ReservaExternaResponse`.
7. De vuelta en el servicio: si el restaurante la confirmó, se guarda en el portal (SP `ins_reserva_restaurante`) con el código del restaurante y el costo.
8. El controller responde **201** si quedó confirmada o **409** si el restaurante la rechazó (por ejemplo, sin lugar).

### Ejemplo 2: sincronizar las sucursales (sincronización)

`POST /api/admin/restaurantes/2/actualizar-sucursales`:

1. **`AdminTaskController`** llama a `RestauranteService.actualizarSucursales(2)`.
2. **`RestauranteServiceImpl`** pide la facade (igual que en el ejemplo 1) y llama a `obtenerSucursales()`.
3. **`RestauranteSoapFacadeImpl`** (Perukai es SOAP) llama a la operación `obtenerSucursales` y devuelve una lista de `SucursalExterna`.
4. El servicio recorre la lista y guarda cada sucursal con **`RestauranteRepository.insUpdSucursal`** → SP `ins_upd_sucursal_restaurante`.
5. El SP inserta o actualiza la sucursal, sus turnos, zonas y preferencias.
6. Responde cuántas sucursales se guardaron.

---

## 6. Para qué sirve cada carpeta y cada clase

Paquete base: `src/main/java/ar/edu/ubp/das/ristorino/`.

### Raíz

| Clase | Para qué sirve |
|---|---|
| `RistorinoApplication` | Punto de entrada. Se ejecuta para levantar el backend |

### `controllers/`: la puerta de entrada HTTP

Reciben el pedido, llaman al servicio y arman la respuesta HTTP (código + cuerpo). No tienen lógica de negocio.

| Clase | Ruta base | Para qué sirve |
|---|---|---|
| `AuthController` | `/api/v1/auth` | Registro, login y logout |
| `ClienteController` | `/api/v1/clientes` | Perfil del cliente logueado |
| `CatalogoController` | `/api/v1/catalogo` | Listas para los formularios: preferencias, localidades, estados de reserva |
| `RestauranteController` | `/api/v1/restaurantes` | Restaurantes, sucursales y disponibilidad |
| `BusquedaController` | `/api/v1/busqueda` | Búsqueda por filtros y en lenguaje natural |
| `ContenidoController` | `/api/v1/contenidos` | Promociones vigentes y registro de clics |
| `ReservaController` | `/api/v1/reservas` | Confirmar reservas y ver las del cliente |
| `AdminTaskController` | `/api/admin/restaurantes` | Disparar a mano la sincronización con los restaurantes |

### `services/` y `services/impl/`: la lógica de negocio

Cada servicio es una interfaz (`services/`) con su implementación (`services/impl/`), igual que en indecWS. Los controllers dependen de la interfaz.

| Servicio | Para qué sirve |
|---|---|
| `AuthService` | Valida el registro, guarda la contraseña con BCrypt, verifica el login y emite o revoca el token |
| `RestauranteService` | Datos de restaurantes y sucursales, disponibilidad en tiempo real, sincronización de sucursales. Tiene `obtenerFacade(nro)`, que usan los otros servicios para hablar con un restaurante |
| `BusquedaService` | Búsqueda por filtros. La de lenguaje natural le pide los filtros a la IA y después busca igual |
| `ContenidoService` | Promociones vigentes, clics, sincronización de contenidos, notificación de clics, generación de textos con IA |
| `ReservaService` | Confirma la reserva con el restaurante y la guarda en el portal; lista las reservas del cliente |
| `CatalogoService` | Listas fijas (preferencias, localidades, estados) |

### `repository/`: el acceso a la base

Cada método llama a **un** stored procedure con `SimpleJdbcCallFactory`. No hay SQL escrito en Java.

| Clase | Para qué sirve | SPs que usa |
|---|---|---|
| `RestauranteRepository` | Restaurantes, configuración de integración, sucursales, búsqueda | `get_restaurantes`, `get_restaurante`, `get_configuracion_restaurante`, `get_sucursales_restaurante`, `buscar_restaurantes`, `ins_upd_sucursal_restaurante` |
| `ContenidoRepository` | Contenidos y clics | `get_contenidos_vigentes`, `ins_upd_contenido_restaurante`, `get_contenidos_sin_generar`, `upd_contenido_promocional`, `ins_click_contenido`, `get_clicks_pendientes`, `upd_click_notificado` |
| `ClienteRepository` | Clientes | `ins_cliente`, `get_cliente_por_correo`, `get_cliente` |
| `ReservaRepository` | Reservas y costos | `get_costo_vigente`, `ins_reserva_restaurante`, `get_reservas_cliente` |
| `CatalogoRepository` | Catálogos | `get_preferencias`, `get_localidades`, `get_estados_reservas` |

### `model/`: la comunicación con los sistemas externos

Acá está todo lo que habla con los restaurantes y con la IA. **No accede a la base del portal.**

| Clase | Para qué sirve |
|---|---|
| `RestauranteFacade` | Interfaz con lo que el portal le pide a cualquier restaurante: `obtenerRestaurante`, `obtenerSucursales`, `consultarDisponibilidad`, `registrarReserva`, `obtenerContenidos`, `notificarClick` |
| `rest/RestauranteRestFacadeImpl` | Implementación para restaurantes REST: arma cada llamada HTTP con `Httpful` |
| `utils/Httpful` | Cliente REST de la cátedra (guía M2-2-5, Jersey + Gson). Arma URL, parámetros, header `Authorization: Bearer` y convierte el JSON |
| `soap/RestauranteSoapFacadeImpl` | Implementación para restaurantes SOAP: llama a cada operación con `SOAPClient` |
| `utils/SOAPClient` | Cliente SOAP dinámico de la cátedra (guía M2-2-6, JAX-WS + JAXB + WS-Security). Arma el sobre SOAP y convierte el XML |
| `RestauranteConfig` | Configuración común de un restaurante: número, tipo de servicio, URL, usuario y contraseña |
| `restauranteConfig/RestConfig` | Configuración de un restaurante REST (no agrega datos) |
| `restauranteConfig/SoapConfig` | Configuración de un restaurante SOAP: agrega namespace, nombre del servicio y del puerto del WSDL |
| `mappers/CustomRowMapper` | Convierte la fila de `get_configuracion_restaurante` en `RestConfig` o `SoapConfig` según `tipo_servicio` |
| `ia/MotorIAFacade` | Interfaz con lo que el portal le pide a la IA: interpretar una búsqueda y generar textos promocionales |
| `ia/MotorIARestFacadeImpl` | Implementación pendiente: hoy lanza `UnsupportedOperationException` (los endpoints responden 501) |

### `beans/`: los datos que viajan

Clases con Lombok `@Data` (getters, setters y `toString` automáticos).

| Grupo | Clases | Para qué sirven |
|---|---|---|
| Pedidos del frontend | `RegistroRequest`, `LoginRequest`, `FiltrosBusqueda`, `FiltroPreferencia`, `BusquedaRequest`, `ReservaRequest`, `ClickRequest`, `PreferenciaCliente` | Lo que manda Angular en el cuerpo del pedido |
| Respuestas al frontend | `RegistroResponse`, `LoginResponse`, `ReservaResponse`, `Restaurante`, `Sucursal`, `ResultadoBusqueda`, `Contenido`, `ReservaCliente`, `Cliente`, `ValorPreferencia`, `Localidad`, `EstadoReserva` | Lo que devuelven los endpoints (la mayoría sale directo de un SP) |
| Internos | `Costo`, `ClickPendiente`, `ContenidoSinGenerar` | Datos que usan los servicios y no se exponen |
| **`restaurante/`** | `RestauranteExterno`, `SucursalExterna`, `DisponibilidadExterna`, `ReservaExternaRequest`/`Response`, `ContenidoExterno`, `ClickExternoRequest`/`Response` | **Contrato con los restaurantes.** Llevan `@XmlRootElement` porque el cliente SOAP los convierte a XML y desde XML con JAXB (igual que `ListaPrecios` en indecWS). Para los REST, Gson ignora esas anotaciones |

### `components/`: piezas reutilizables que maneja Spring

| Clase | Para qué sirve |
|---|---|
| `SimpleJdbcCallFactory` | Ejecuta stored procedures. Copiado sin cambios de indecWS |
| `RestauranteFacadeFactory` | Crea la facade REST o SOAP de un restaurante según su configuración |
| `RestClientFactory` / `SOAPClientFactory` | Crean un `Httpful` (con el token) / `SOAPClient` (con usuario y clave) a partir de la configuración |
| `JwtTokenProvider` | Genera los tokens de login y registra los revocados con logout (la validación la hace `SecurityConfig`) |

### `config/`: configuración web

| Clase | Para qué sirve |
|---|---|
| `WebConfig` | Habilita CORS para el frontend (`localhost:4200`, clase 18) y la negociación de contenido (JSON por defecto) |
| `SecurityConfig` | Spring Security con JWT (guía M2-2-4): `/api/v1/reservas/**`, `/api/v1/clientes/**` y `/api/v1/auth/logout` requieren token; sin token válido responde 401 |

### `aspects/`, `batch/` y `utils/`

| Clase | Para qué sirve |
|---|---|
| `aspects/AuditAspect` | Escribe en consola cada vez que se crea la facade de un restaurante (auditoría con AOP, igual que indecWS) |
| `batch/ActualizarSucursalesEjecutor` | Sincroniza las sucursales de todos los restaurantes ([sección 9](#9-procesos-batch)) |
| `batch/ActualizarContenidosEjecutor` | Sincroniza los contenidos de todos los restaurantes |
| `batch/NotificarClicksEjecutor` | Informa a cada restaurante los clics pendientes |
| `utils/Constantes` | Valores fijos: `REST`, `SOAP`, `CONFIRMADA`, `RECHAZADA`, `COSTO_RESERVA`, idioma por defecto, días de vigencia de un contenido, mensajes |
| `utils/IdiomaUtils` | Convierte el header `Accept-Language` (`es-AR,es;q=0.9`) en `es` o `en` |

---

## 7. Endpoints: para qué sirven y cómo usarlos

### Reglas generales

- **Idioma:** los endpoints que devuelven textos traducibles leen el header `Accept-Language`. Si mandás `en` vienen en inglés; si no mandás nada, en español.
- **Sesión:** las rutas marcadas con 🔒 necesitan el header `Authorization: Bearer <token>`. El token se obtiene con el login.
- **Fechas y horas:** `yyyy-MM-dd` y `HH:mm`.
- **Códigos de respuesta:**

| Código | Cuándo |
|---|---|
| 200 / 201 | Todo bien (201 cuando se creó algo: registro, reserva, clic) |
| 204 | Logout correcto (sin cuerpo) |
| 400 | Datos inválidos en el registro |
| 401 | Falta el token, es inválido, venció, se cerró la sesión, o el login es incorrecto |
| 404 | El restaurante no existe |
| 409 | El restaurante rechazó la reserva (por ejemplo, sin lugar) |
| 500 | Error interno (por ejemplo, la base no responde) |
| 501 | Funcionalidad de IA todavía no implementada |
| 502 | El servicio del restaurante no respondió o devolvió un error |

### 7.1 Autenticación y cliente

**`POST /api/v1/auth/registro`**: alta de un cliente (Req 1).

```json
{
  "apellido": "Ferreyra", "nombre": "Lucía", "correo": "lucia@mail.com",
  "clave": "1234", "confirmacionClave": "1234",
  "telefonos": "351-5550000", "nroLocalidad": 1,
  "preferencias": [ { "codCategoria": 1, "nroValorDominio": 11, "observaciones": "Me encanta la peruana" } ]
}
```

- `preferencias`: valores de `GET /api/v1/catalogo/preferencias`. Es opcional.
- Responde **201** `{ "nroCliente": 1, "estado": "REGISTRADO", "motivo": null }`.
- Responde **400** con `estado: "RECHAZADO"` y `motivo`: `CLAVES_NO_COINCIDEN` o `CORREO_EXISTENTE`.

**`POST /api/v1/auth/login`**: iniciar sesión (Req 2).

- Cuerpo: `{ "correo": "lucia@mail.com", "clave": "1234" }`.
- Responde **200** `{ "token": "eyJ...", "nroCliente": 1, "apellido": "...", "nombre": "...", "correo": "..." }`.
- Responde **401** sin cuerpo si el correo o la clave no coinciden, o el cliente está deshabilitado.

**`POST /api/v1/auth/logout`** 🔒: cerrar sesión (Req 5). Invalida el token; responde **204**.

**`GET /api/v1/clientes/me`** 🔒: datos del cliente logueado (sin la clave).

### 7.2 Catálogos

Sirven para llenar los formularios del frontend.

| Endpoint | Para qué sirve | Idioma |
|---|---|---|
| `GET /api/v1/catalogo/preferencias` | Categorías y valores de preferencias (tipo de cocina, especialidad, estilo, nivel de precio). Para el registro y los filtros de búsqueda | ✔ |
| `GET /api/v1/catalogo/localidades` | Localidades, para el registro | — |
| `GET /api/v1/catalogo/estados-reservas` | Estados de reserva, para filtrar "mis reservas" | ✔ |

### 7.3 Restaurantes y disponibilidad

| Endpoint | Para qué sirve |
|---|---|
| `GET /api/v1/restaurantes` | Lista de restaurantes |
| `GET /api/v1/restaurantes/{nro}` | Un restaurante (404 si no existe) |
| `GET /api/v1/restaurantes/{nro}/sucursales` | Sucursales de un restaurante, con sus zonas, turnos y preferencias (Req 11). Sale de la base del portal: hace falta haber sincronizado antes |
| `GET /api/v1/restaurantes/{nro}/sucursales/{suc}/disponibilidad` | Lugares libres por zona y turno para una fecha (Req 14/37). **Se le pregunta al restaurante en el momento** |

En las sucursales, `zonas` y `preferencias` vienen como texto JSON (así lo arma el SP con `FOR JSON PATH`); el frontend lo convierte con `JSON.parse`. Ejemplo de `zonas`:

```json
[ { "cod_zona": 1, "zona": "Salón", "cant_comensales": 30, "permite_menores": true,
    "turnos": [ { "hora_desde": "20:30", "hora_hasta": "22:00", "permite_menores": true } ] } ]
```

Parámetros de la disponibilidad:

| Parámetro | Obligatorio | Para qué |
|---|---|---|
| `fecha` | sí | Día de la reserva (`yyyy-MM-dd`) |
| `adultos` | sí | Cantidad de adultos |
| `menores` | no (0) | Cantidad de menores. Si hay menores, solo se muestran como disponibles las zonas y turnos que los aceptan |
| `codZona` | no | Para ver una sola zona |

Devuelve una fila por zona y turno, con `lugaresLibres` y `disponible`. `disponible` es `true` si entran todos, si la zona y el turno aceptan menores (cuando hay menores) y si todavía se llega con la anticipación mínima de la sucursal (`minTolerenciaReserva`).

### 7.4 Búsqueda

**`POST /api/v1/busqueda/filtros`**: búsqueda por preferencias y barrio (Req 9).

```json
{ "valoresDominio": [ { "codCategoria": 1, "nroValorDominio": 11 } ], "barrio": "Nueva Córdoba" }
```

- Los dos campos son opcionales; sin filtros devuelve todas las sucursales.
- Devuelve sucursales ordenadas por `coincidencias` (cuántos filtros cumple cada una).

**`POST /api/v1/busqueda/lenguaje-natural`**: búsqueda escrita libremente (Req 10).

- Cuerpo: `{ "texto": "quiero sushi para dos en Nueva Córdoba" }`.
- Responde **501** hasta que se implemente la IA. La idea es que la IA traduzca el texto a los mismos filtros de arriba.

### 7.5 Contenidos y clics

**`GET /api/v1/contenidos`**: promociones vigentes para la portada (Req 12).

- `?nroRestaurante=2` filtra las de un restaurante.
- Si existe el texto generado por IA, muestra ese; si no, el que mandó el restaurante.
- Si no hay versión en el idioma pedido, muestra la de español.

**`POST /api/v1/contenidos/clicks`**: registra que alguien hizo clic en una promoción (Req 50).

- Cuerpo: `{ "nroRestaurante": 1, "nroIdioma": 1, "nroContenido": 1 }` (los tres datos vienen en cada contenido de la lista anterior).
- La sesión es opcional: si viene el token, el clic queda asociado al cliente.
- Responde **201** `{ "nroClick": 7 }`.
- El clic **no** se le avisa al restaurante en ese momento: queda pendiente y se notifica después (sección 7.7). Así, si el restaurante no responde, el clic no se pierde.

### 7.6 Reservas 🔒

**`POST /api/v1/reservas`**: confirmar una reserva (Req 15/38).

```json
{ "nroRestaurante": 2, "nroSucursal": 1, "codZona": 1, "fecha": "2026-10-21", "hora": "20:30",
  "cantAdultos": 2, "cantMenores": 0 }
```

- `hora` es el inicio de un turno (`hora_desde` de la disponibilidad).
- Responde **201** con `estado: "CONFIRMADA"`, el `nroReserva` del portal, el `codReservaSucursal` que generó el restaurante y el `costoReserva`.
- Responde **409** con `estado: "RECHAZADA"` y el `motivo` que dio el restaurante: `SIN_DISPONIBILIDAD`, `MENORES_NO_PERMITIDOS`, `ZONA_TURNO_INEXISTENTE`, `SUCURSAL_INEXISTENTE`, `COMENSALES_INVALIDOS` o `FECHA_PASADA`. En ese caso no se guarda nada en el portal.
- Responde **502** si el restaurante no respondió.

**`GET /api/v1/reservas`**: reservas del cliente logueado (Req 16).

- Filtros opcionales: `codEstado` (de `estados-reservas`), `fechaDesde`, `fechaHasta`.

### 7.7 Administración (sincronización)

No piden sesión: son tareas internas, equivalentes a los procesos batch.

| Endpoint | Para qué sirve | Req |
|---|---|---|
| `POST /api/admin/restaurantes/{nro}/actualizar-sucursales` | Trae las sucursales del restaurante y las guarda en el portal | 45 |
| `POST /api/admin/restaurantes/{nro}/actualizar-contenidos` | Trae las promociones nuevas del restaurante y las guarda con 30 días de vigencia | 46 |
| `POST /api/admin/restaurantes/{nro}/notificar-clicks` | Le informa al restaurante los clics pendientes y los marca como notificados | 50 |
| `POST /api/admin/restaurantes/generar-contenidos` | Genera con IA los textos promocionales (responde 501 hasta implementar la IA) | 34/48 |

Responden un texto con la cantidad procesada, por ejemplo `Sucursales actualizadas para el restaurante 2: 2`.

> **Ojo con los contenidos:** cada restaurante entrega sus contenidos **una sola vez** (los marca como publicados). Si volvés a llamar a `actualizar-contenidos`, devuelve 0. Para volver a traerlos, recreá la base del restaurante.

---

## 8. Colección de Postman

Archivo: `Doc/Ristorino.backend.postman_collection.json`.

### Cómo importarla

En Postman: **Import** → arrastrá el archivo. Aparece la colección **Ristorino - Backend**.

### Cómo correrla

- **Toda junta:** clic derecho en la colección → **Run collection** → **Run**. Muestra qué tests pasaron.
- **Por partes:** abrí cada carpeta y ejecutá los requests en orden.

Las carpetas están pensadas para correrse **en orden**:

| Carpeta | Qué hace | Necesita |
|---|---|---|
| 0. Admin | Sincroniza sucursales y contenidos de los 4 restaurantes | Los 4 servicios levantados |
| 1. Autenticación | Registra un cliente nuevo, prueba errores y hace login (guarda el token) | — |
| 2. Catálogos | Listas en español e inglés | — |
| 3. Restaurantes | Restaurantes, sucursales y disponibilidad (REST y SOAP) | Carpeta 0 |
| 4. Búsqueda | Búsqueda por filtros y en lenguaje natural | Carpeta 0 |
| 5. Contenidos y clics | Promociones, clics con y sin sesión, notificación al restaurante | Carpetas 0 y 1 |
| 6. Reservas | Reservas confirmadas, rechazada, sin sesión, y "mis reservas" | Carpeta 1 |
| 7. Cierre de sesión | Logout y prueba de que el token ya no sirve | Carpeta 1 |

### Variables (pestaña *Variables* de la colección)

| Variable | Para qué sirve | Quién la carga |
|---|---|---|
| `baseUrl` | Dirección del backend (`http://localhost:8080`) | Fija; cambiala si usás otro puerto |
| `correo` / `clave` | Credenciales del cliente de prueba | El registro genera un correo nuevo en cada corrida |
| `token` | Token de sesión | El login. La colección lo manda en todos los requests |
| `nroCliente` | Número del cliente registrado | El registro |
| `fechaReserva` | Hoy + 7 días | Se calcula antes de cada request |
| `hoy` | Fecha de hoy | Se calcula antes de cada request |
| `clickRestaurante`, `clickIdioma`, `clickContenido` | Contenido sobre el que se hace clic | "Contenidos vigentes" guarda el primero de la lista |

Cada request tiene una **descripción** (pestaña *Docs*) con lo que hace y qué mirar en la respuesta, y un **test** con el código esperado.

---

## 9. Procesos batch

Son clases con `main` que levantan Spring **sin servidor web**, hacen la tarea para **todos** los restaurantes y terminan. Sirven para programarlas (por ejemplo, con el programador de tareas del sistema operativo) y que la sincronización sea automática. Si un restaurante no responde, siguen con los demás.

| Clase | Qué hace | Cada cuánto conviene |
|---|---|---|
| `ActualizarSucursalesEjecutor` | Sucursales, turnos, zonas y preferencias | Una vez al día |
| `ActualizarContenidosEjecutor` | Promociones nuevas | Al menos una vez al día (lo pide el caso) |
| `NotificarClicksEjecutor` | Avisa los clics pendientes | Cada hora, o una vez al día |

**Cómo ejecutarlas en IntelliJ:** abrí la clase y tocá ▶ al lado del `main`. Usan el mismo `application.properties`, así que la base tiene que estar levantada. Pueden correr mientras el backend también está corriendo, porque no abren el puerto 8080.

---

## 10. Base de datos y stored procedures

### Scripts

| Archivo | Para qué sirve |
|---|---|
| `Doc/scripts/final-db-creation.sql` | Crea la base, las tablas (según `RISTORINO-Logico.pdf`, con los nombres tal cual) y los datos iniciales. Borra todo si ya existía |
| `Doc/scripts/procedimientos.sql` | Crea o reemplaza todos los stored procedures |

### Datos iniciales que cargan los scripts

| Qué | Valores |
|---|---|
| Idiomas | 1 = Español (`es`), 2 = English (`en`) |
| Restaurantes | 1 La Bella Pizza, 2 Perukai, 3 La Fábrica Burger, 4 Sabores del Norte |
| Configuración de integración | URL, protocolo, usuario y contraseña de cada restaurante (y namespace, servicio y puerto para los SOAP) |
| Preferencias | 4 categorías (tipo de cocina, especialidades, estilo, nivel de precio) con sus valores, en los dos idiomas. Los nombres coinciden con los catálogos de los restaurantes, porque la sincronización los vincula por nombre |
| Estados de reserva | 1 Pendiente, 2 Cancelada, 3 Sin evaluar, 4 Evaluada |
| Costos | Reserva: $500; clic: $100 |
| Localidades | Córdoba |

### Configuración de un restaurante en la base

`configuracion_restaurantes` guarda pares atributo/valor por restaurante (los atributos están en `atributos`). El SP `get_configuracion_restaurante` los junta en una fila:

| Atributo | Ejemplo REST (La Bella Pizza) | Ejemplo SOAP (Perukai) |
|---|---|---|
| `TIPO_SERVICIO` | `REST` | `SOAP` |
| `URL_SERVICIO` | `http://localhost:8081/api/v1` | `http://localhost:8082/services/perukai.wsdl` |
| `TOKEN` | JWT generado con `security.jwt.secret` del restaurante | — |
| `USUARIO` / `PASSWORD` | — | `usr_admin` / `pwd_admin` (WS-Security) |
| `NAMESPACE` | — | namespace del WSDL |
| `SERVICE_NAME` / `PORT_NAME` | — | `PerukaiWSPortService` / `PerukaiWSPortSoap11` |

### Stored procedures

| SP | Para qué sirve | Lo usa |
|---|---|---|
| `get_restaurantes` / `get_restaurante` | Lista de restaurantes / uno | `RestauranteRepository` |
| `get_configuracion_restaurante` | URL, protocolo y credenciales de un restaurante, en una fila | `RestauranteRepository.findRestauranteConfig` |
| `ins_upd_sucursal_restaurante` | Inserta o actualiza una sucursal recibida del restaurante, con turnos, zonas y preferencias (que llegan como JSON y se leen con `OPENJSON`) | Sincronización de sucursales |
| `get_sucursales_restaurante` | Sucursales con zonas, turnos y preferencias en el idioma pedido | `GET .../sucursales` |
| `buscar_restaurantes` | Sucursales que cumplen los filtros, ordenadas por coincidencias | Búsqueda |
| `ins_upd_contenido_restaurante` | Guarda un contenido recibido del restaurante | Sincronización de contenidos |
| `get_contenidos_vigentes` | Promociones vigentes en el idioma pedido | `GET /contenidos` |
| `get_contenidos_sin_generar` / `upd_contenido_promocional` | Contenidos sin texto de IA / guardar el texto generado | Generación con IA (pendiente) |
| `ins_click_contenido` | Registra un clic con el costo del contenido | `POST /contenidos/clicks` |
| `get_clicks_pendientes` / `upd_click_notificado` | Clics sin avisar / marcarlos como avisados | Notificación de clics |
| `ins_cliente` | Alta de cliente con sus preferencias (la clave llega ya hasheada). Rechaza correos repetidos | Registro |
| `get_cliente_por_correo` / `get_cliente` | Cliente por correo (para el login) / por número | Login, perfil, reservas |
| `get_preferencias` / `get_localidades` / `get_estados_reservas` | Catálogos | `CatalogoRepository` |
| `get_costo_vigente` | Costo vigente hoy de una reserva o un clic | Reservas |
| `ins_reserva_restaurante` | Guarda en el portal una reserva ya confirmada por el restaurante | Reservas |
| `get_reservas_cliente` | Reservas del cliente con filtros | "Mis reservas" |

---

## 11. Cómo hacer los cambios más comunes

### Agregar un restaurante nuevo (REST o SOAP)

No hace falta tocar Java:

1. Insertar el restaurante en `restaurantes`.
2. Insertar sus filas en `configuracion_restaurantes` (copiá las de uno del mismo tipo y cambiá URL, credenciales y, si es SOAP, namespace, servicio y puerto).
3. Llamar a `actualizar-sucursales` y `actualizar-contenidos` con su número.

Funciona siempre que el restaurante respete el mismo contrato que los otros (mismas operaciones y campos).

### Agregar un endpoint nuevo que lee de la base

Se recorren las capas de abajo hacia arriba:

1. **SP** en `procedimientos.sql` (y ejecutarlo). Los nombres de columna en `snake_case` se mapean solos a los campos en `camelCase` del bean.
2. **Bean** en `beans/` con `@Data`, con un campo por columna.
3. **Método en el repository**, copiando uno parecido:
   ```java
   public List<MiBean> getAlgo(int nro) {
       SqlParameterSource params = new MapSqlParameterSource().addValue("nro", nro);
       return simpleJdbcCallFactory.executeQuery("mi_sp", "dbo", params, "resultSet", MiBean.class);
   }
   ```
4. **Método en la interfaz del servicio** y en su `impl`, que llama al repository.
5. **Método en el controller** con `@GetMapping` / `@PostMapping`.
6. Si necesita sesión, agregar la ruta en `SecurityConfig` (`.requestMatchers(...).authenticated()`) y recibir `@AuthenticationPrincipal Jwt jwt` (el `nroCliente` es `jwt.getSubject()`).

### Agregar una operación nueva con los restaurantes

1. Agregar la operación en los servicios de los restaurantes (ver `servicios-restaurantes/`).
2. Crear los beans en `beans/restaurante/` (con `@XmlRootElement` si viajan por SOAP).
3. Agregar el método en `RestauranteFacade` e implementarlo en **las dos** facades.
4. Llamarlo desde un servicio con `restauranteService.obtenerFacade(nro).miOperacion(...)`.

### Agregar un campo a una respuesta

1. Agregar la columna al `SELECT` del SP.
2. Agregar el campo al bean (en `camelCase`).

### Cambiar los costos

Los costos están en la tabla `costos` con fecha de vigencia. Para cambiar un costo desde hoy, insertá una fila nueva con `fecha_ini_vigencia` = hoy: `get_costo_vigente` toma la vigente más reciente. Las reservas anteriores mantienen el costo con el que se guardaron.

### Agregar un idioma

1. Insertarlo en `idiomas` (por ejemplo, `3, 'Português', 'pt'`).
2. Cargar las traducciones en las tablas `idiomas_*`.

No hay que tocar Java: `IdiomaUtils` toma las dos primeras letras del header `Accept-Language`, y los SPs buscan ese código en `idiomas`. Si un idioma no existe o le falta una traducción, los SPs muestran el texto en español.

---

## 12. Problemas comunes

| Síntoma | Causa probable | Solución |
|---|---|---|
| El backend no arranca: `Login failed for user 'sa'` o `Connection refused` | SQL Server apagado o credenciales distintas | Levantá SQL Server y revisá usuario, contraseña y puerto en `application.properties` |
| `Cannot open database "ristorino"` | No se ejecutó `final-db-creation.sql` | Ejecutalo |
| Error `Could not find stored procedure` | No se ejecutó `procedimientos.sql` | Ejecutalo |
| `Port 8080 was already in use` | Otro proceso usa el 8080 (puede ser el mismo backend abierto dos veces) | Cerralo, o cambiá `server.port` y `baseUrl` en Postman |
| Aviso de Jersey: `Jersey-HK2 module is missing` | Igual que en indecWS | No afecta: el cliente REST funciona igual |
| 401 en un endpoint con 🔒 | Falta el token, venció (120 min), se hizo logout, o se reinició el backend (los tokens revocados se guardan en memoria) | Hacé login de nuevo |
| 502 en disponibilidad o reservas | El servicio del restaurante no está levantado o la URL de `configuracion_restaurantes` está mal | Levantalo y probá su URL directamente |
| Sucursales o contenidos vacíos | No se sincronizó | Corré la carpeta 0 de Postman |
| `actualizar-contenidos` devuelve 0 | El restaurante ya entregó todos sus contenidos | Es lo esperado (ver 7.7). Para repetir, recreá la base del restaurante |
| Reserva siempre `RECHAZADA` | Zona u horario que no existe, menores en una zona que no los acepta, o fecha pasada | Mirá el `motivo` y consultá antes la disponibilidad |
| Acentos rotos en la base | Script ejecutado con `sqlcmd` sin UTF-8 | Usá `sqlcmd -f 65001` o ejecutalo desde SSMS |
| La búsqueda no encuentra por preferencias | Los nombres del catálogo del restaurante no coinciden con los del portal | Revisá que los valores sean iguales en los dos catálogos |

---

## 13. Pendiente

- **Motor de IA:** `MotorIARestFacadeImpl` está armado pero sin implementar. Afecta a la búsqueda en lenguaje natural (Req 10/35) y a la generación de textos promocionales (Req 34/48), que responden 501.
- **Requerimientos opcionales:** modificar o cancelar reservas, pedidos, reseñas, favoritos y administración.
- **Frontend Angular:** hay que adaptarlo a las rutas `/api/v1`.
