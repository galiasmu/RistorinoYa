# Ristorino — Backend

Backend del portal Ristorino. Toma como guía la estructura de `indecWS`.

| Documento | Qué tiene |
|---|---|
| **README.md** (este) | Cómo levantarlo y probarlo rápido |
| **[GUIA.md](GUIA.md)** | Para qué sirve cada carpeta, clase, endpoint y stored procedure; cómo usar Postman y los batch; cómo hacer los cambios más comunes; problemas comunes |
| **[ARQUITECTURA.md](ARQUITECTURA.md)** | Capas, patrones y decisiones de diseño (para explicar la arquitectura) |
| `Doc/Ristorino.backend.postman_collection.json` | Colección de Postman: cada request explica qué hace y qué respuesta esperar |

## Cómo levantarlo

1. **Base de datos.** En SSMS o Azure Data Studio, ejecutá en este orden:
   1. `Doc/scripts/final-db-creation.sql`: crea la base `ristorino` con sus tablas y datos iniciales.
   2. `Doc/scripts/procedimientos.sql`: crea los stored procedures.

   Si usás sqlcmd, agregá `-f 65001` para que no se rompan los acentos. Usuario y contraseña de SQL Server: `sa` / `Password.1234`. Si son otros, cambialos en `application.properties`.
2. **Servicios de los restaurantes.** Levantá los 4 servicios de `servicios-restaurantes/` (puertos 8081 a 8084), cada uno con su base.
3. **Backend.** Abrí el proyecto en IntelliJ y ejecutá `RistorinoApplication`. Queda en el puerto **8080**.
   - Como en indecWS, el proyecto usa `spring-boot-starter-data-jdbc`, así que la base tiene que estar levantada antes de arrancar.
4. **Cargar los datos de los restaurantes** (sucursales y contenidos), una vez por restaurante (`{nro}` = 1 a 4):
   ```
   POST http://localhost:8080/api/admin/restaurantes/{nro}/actualizar-sucursales
   POST http://localhost:8080/api/admin/restaurantes/{nro}/actualizar-contenidos
   ```
   También se puede hacer con los ejecutores de `batch/`: `ActualizarSucursalesEjecutor` y `ActualizarContenidosEjecutor`.

## Flujo de prueba

**Con Postman:** importá `Doc/Ristorino.backend.postman_collection.json` y corré las carpetas en orden (0 → 7), una por una o todas juntas con el *Collection Runner*.
- La carpeta 0 sincroniza sucursales y contenidos de los 4 restaurantes.
- El registro usa un correo distinto en cada corrida, y el login guarda el token solo; todos los requests siguientes lo mandan.
- La fecha de reserva se calcula sola (hoy + 7 días). Cada request tiene un test con el código de respuesta esperado (incluidos los casos de error: 400, 401, 404, 409 y 501 para la IA).

**A mano:**

1. `POST /api/v1/auth/registro`
   ```json
   {"apellido":"Ferreyra","nombre":"Lucía","correo":"lucia@mail.com","clave":"1234","confirmacionClave":"1234",
    "telefonos":"351-5550000","nroLocalidad":1,"preferencias":[{"codCategoria":1,"nroValorDominio":5}]}
   ```
2. `POST /api/v1/auth/login` con `{"correo":"lucia@mail.com","clave":"1234"}`. Devuelve un `token`.
3. `GET /api/v1/contenidos`: promociones vigentes.
4. `GET /api/v1/restaurantes/2/sucursales/1/disponibilidad?fecha=2026-10-20&adultos=2`: consulta en tiempo real a Perukai (SOAP).
5. `POST /api/v1/reservas` con el header `Authorization: Bearer <token>`:
   ```json
   {"nroRestaurante":2,"nroSucursal":1,"codZona":1,"fecha":"2026-10-20","hora":"20:30","cantAdultos":2,"cantMenores":0}
   ```
6. `GET /api/v1/reservas` con el mismo token.
7. `POST /api/v1/contenidos/clicks` con `{"nroRestaurante":1,"nroIdioma":1,"nroContenido":1}`, y después `POST /api/admin/restaurantes/1/notificar-clicks`.

## Pendiente

- **Motor de IA** (`model/ia/MotorIARestFacadeImpl`): la búsqueda en lenguaje natural (Req 10/35) y la generación de contenidos (Req 34/48) responden **501** hasta que se implemente. La URL, la clave y el modelo se configuran en `application.properties` (`ia.api.*`).
- **Requerimientos opcionales:** modificar o cancelar reservas, pedidos, reseñas, favoritos y administración.
