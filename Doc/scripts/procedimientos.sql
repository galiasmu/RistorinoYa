-- ============================================================
-- STORED PROCEDURES — BD ristorino
-- Ejecutar DESPUÉS de final-db-creation.sql.
-- Columnas en snake_case → BeanPropertyRowMapper las mapea a camelCase.
-- Fechas y horas se devuelven como VARCHAR (yyyy-MM-dd / HH:mm).
-- ============================================================

USE ristorino;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- ============================================================
-- RESTAURANTES E INTEGRACIÓN
-- ============================================================

CREATE OR ALTER PROCEDURE dbo.get_restaurantes
AS
BEGIN
    SET NOCOUNT ON;
    SELECT r.nro_restaurante, r.razon_social, r.cuit
    FROM dbo.restaurantes r
    ORDER BY r.razon_social;
END;
GO

CREATE OR ALTER PROCEDURE dbo.get_restaurante
    @nro_restaurante INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT r.nro_restaurante, r.razon_social, r.cuit, r.descripcion
    FROM dbo.restaurantes r
    WHERE r.nro_restaurante = @nro_restaurante;
END;
GO

-- Configuración de la integración (tabla atributo/valor pivoteada en una fila).
-- La mapea CustomRowMapper a RestConfig o SoapConfig según tipo_servicio.
CREATE OR ALTER PROCEDURE dbo.get_configuracion_restaurante
    @nro_restaurante INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT @nro_restaurante AS nro_restaurante,
           MAX(CASE WHEN cod_atributo = 'TIPO_SERVICIO' THEN valor END) AS tipo_servicio,
           MAX(CASE WHEN cod_atributo = 'URL_SERVICIO'  THEN valor END) AS url_servicio,
           MAX(CASE WHEN cod_atributo = 'USUARIO'       THEN valor END) AS username,
           MAX(CASE WHEN cod_atributo = 'PASSWORD'      THEN valor END) AS password,
           MAX(CASE WHEN cod_atributo = 'TOKEN'         THEN valor END) AS token,
           MAX(CASE WHEN cod_atributo = 'NAMESPACE'     THEN valor END) AS namespace,
           MAX(CASE WHEN cod_atributo = 'SERVICE_NAME'  THEN valor END) AS service_name,
           MAX(CASE WHEN cod_atributo = 'PORT_NAME'     THEN valor END) AS port_name
    FROM dbo.configuracion_restaurantes
    WHERE nro_restaurante = @nro_restaurante
    HAVING COUNT(*) > 0;
END;
GO

-- Sincronización de una sucursal (Req 45): la llama RestauranteRepository.insUpdSucursal con lo que
-- devuelve obtenerSucursales del restaurante. Los datos anidados llegan como JSON (FOR JSON PATH
-- del lado del restaurante) y se leen con OPENJSON.
CREATE OR ALTER PROCEDURE dbo.ins_upd_sucursal_restaurante
    @nro_restaurante         INT,
    @nro_sucursal            INT,
    @nom_sucursal            VARCHAR(100),
    @calle                   VARCHAR(100),
    @nro_calle               INT,
    @barrio                  VARCHAR(100),
    @nom_localidad           VARCHAR(100),
    @cod_postal              VARCHAR(10)   = NULL,
    @telefonos               VARCHAR(100)  = NULL,
    @total_comensales        INT,
    @min_tolerencia_reserva  INT,
    @nom_categoria           VARCHAR(100)  = NULL,   -- nivel de precio
    @turnos                  NVARCHAR(MAX) = NULL,
    @zonas                   NVARCHAR(MAX) = NULL,
    @tipos_comidas           NVARCHAR(MAX) = NULL,
    @especialidades          NVARCHAR(MAX) = NULL,
    @estilos                 NVARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @nro_localidad INT;

    BEGIN TRAN;

    -- Localidad (todas las sucursales del caso están en la provincia de Córdoba)
    SELECT @nro_localidad = nro_localidad FROM dbo.localidades WHERE nom_localidad = @nom_localidad;
    IF @nro_localidad IS NULL
    BEGIN
        INSERT INTO dbo.localidades (nom_localidad, cod_provincia) VALUES (@nom_localidad, 1);
        SET @nro_localidad = SCOPE_IDENTITY();
    END;

    -- Sucursal (nro_sucursal del portal = número del restaurante; cod_sucursal_restaurante lo guarda como texto)
    MERGE dbo.sucursales_restaurantes AS t
    USING (SELECT @nro_restaurante AS nro_restaurante, @nro_sucursal AS nro_sucursal) AS s
       ON t.nro_restaurante = s.nro_restaurante AND t.nro_sucursal = s.nro_sucursal
    WHEN MATCHED THEN
        UPDATE SET nom_sucursal = @nom_sucursal, calle = @calle, nro_calle = @nro_calle, barrio = @barrio,
                   nro_localidad = @nro_localidad, cod_postal = @cod_postal, telefonos = @telefonos,
                   total_comensales = @total_comensales, min_tolerencia_reserva = @min_tolerencia_reserva
    WHEN NOT MATCHED THEN
        INSERT (nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal,
                telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
        VALUES (@nro_restaurante, @nro_sucursal, @nom_sucursal, @calle, @nro_calle, @barrio, @nro_localidad, @cod_postal,
                @telefonos, @total_comensales, @min_tolerencia_reserva, CAST(@nro_sucursal AS VARCHAR(20)));

    -- Turnos
    MERGE dbo.turnos_sucursales_restaurantes AS t
    USING (SELECT CAST(hora_desde AS TIME) AS hora_desde, CAST(hora_hasta AS TIME) AS hora_hasta, habilitado
           FROM OPENJSON(@turnos) WITH (hora_desde VARCHAR(5), hora_hasta VARCHAR(5), habilitado BIT)) AS s
       ON t.nro_restaurante = @nro_restaurante AND t.nro_sucursal = @nro_sucursal AND t.hora_desde = s.hora_desde
    WHEN MATCHED THEN
        UPDATE SET hora_hasta = s.hora_hasta, habilitado = s.habilitado
    WHEN NOT MATCHED THEN
        INSERT (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
        VALUES (@nro_restaurante, @nro_sucursal, s.hora_desde, s.hora_hasta, s.habilitado);

    -- Zonas
    MERGE dbo.zonas_sucursales_restaurantes AS t
    USING (SELECT cod_zona, nom_zona, cant_comensales, permite_menores, habilitada
           FROM OPENJSON(@zonas) WITH (cod_zona INT, nom_zona VARCHAR(100), cant_comensales INT,
                                       permite_menores BIT, habilitada BIT)) AS s
       ON t.nro_restaurante = @nro_restaurante AND t.nro_sucursal = @nro_sucursal AND t.cod_zona = s.cod_zona
    WHEN MATCHED THEN
        UPDATE SET desc_zona = s.nom_zona, cant_comensales = s.cant_comensales,
                   permite_menores = s.permite_menores, habilitada = s.habilitada
    WHEN NOT MATCHED THEN
        INSERT (nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
        VALUES (@nro_restaurante, @nro_sucursal, s.cod_zona, s.nom_zona, s.cant_comensales, s.permite_menores, s.habilitada);

    -- Nombre de la zona en español (el inglés lo completaría la IA o un administrador)
    MERGE dbo.idiomas_zonas_suc_restaurantes AS t
    USING (SELECT cod_zona, nom_zona FROM OPENJSON(@zonas) WITH (cod_zona INT, nom_zona VARCHAR(100))) AS s
       ON t.nro_restaurante = @nro_restaurante AND t.nro_sucursal = @nro_sucursal
      AND t.cod_zona = s.cod_zona AND t.nro_idioma = 1
    WHEN MATCHED THEN UPDATE SET zona = s.nom_zona
    WHEN NOT MATCHED THEN
        INSERT (nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona)
        VALUES (@nro_restaurante, @nro_sucursal, s.cod_zona, 1, s.nom_zona);

    -- Turnos de cada zona (JSON anidado dentro de cada zona)
    MERGE dbo.zonas_turnos_sucurales_restaurantes AS t
    USING (SELECT z.cod_zona, CAST(zt.hora_desde AS TIME) AS hora_desde, zt.permite_menores
           FROM OPENJSON(@zonas) WITH (cod_zona INT, turnos NVARCHAR(MAX) AS JSON) z
           CROSS APPLY OPENJSON(z.turnos) WITH (hora_desde VARCHAR(5), permite_menores BIT) zt) AS s
       ON t.nro_restaurante = @nro_restaurante AND t.nro_sucursal = @nro_sucursal
      AND t.cod_zona = s.cod_zona AND t.hora_desde = s.hora_desde
    WHEN MATCHED THEN UPDATE SET permite_menores = s.permite_menores
    WHEN NOT MATCHED THEN
        INSERT (nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
        VALUES (@nro_restaurante, @nro_sucursal, s.cod_zona, s.hora_desde, s.permite_menores);

    -- Preferencias del restaurante para esta sucursal: se vinculan por nombre con los valores
    -- de dominio de cada categoría (1 tipo de cocina, 2 especialidades, 3 estilo, 4 nivel de precio)
    ;WITH valores AS (
        SELECT 1 AS cod_categoria, nombre FROM OPENJSON(@tipos_comidas)  WITH (nombre VARCHAR(100) '$.nom_tipo_comida')
        UNION
        SELECT 2, nombre FROM OPENJSON(@especialidades) WITH (nombre VARCHAR(100) '$.nom_especialidad')
        UNION
        SELECT 3, nombre FROM OPENJSON(@estilos)        WITH (nombre VARCHAR(100) '$.nom_estilo')
        UNION
        SELECT 4, @nom_categoria WHERE @nom_categoria IS NOT NULL
    ),
    nuevas AS (
        SELECT d.cod_categoria, d.nro_valor_dominio
        FROM valores v
        JOIN dbo.dominio_categorias_preferencias d
          ON d.cod_categoria = v.cod_categoria AND d.nom_valor_dominio = v.nombre
        WHERE NOT EXISTS (SELECT 1 FROM dbo.preferencias_restaurantes p
                          WHERE p.nro_restaurante = @nro_restaurante AND p.nro_sucursal = @nro_sucursal
                            AND p.cod_categoria = d.cod_categoria AND p.nro_valor_dominio = d.nro_valor_dominio)
    )
    INSERT INTO dbo.preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, nro_sucursal)
    SELECT @nro_restaurante, n.cod_categoria, n.nro_valor_dominio,
           (SELECT COALESCE(MAX(nro_preferencia), 0) FROM dbo.preferencias_restaurantes WHERE nro_restaurante = @nro_restaurante)
           + ROW_NUMBER() OVER (ORDER BY n.cod_categoria, n.nro_valor_dominio),
           @nro_sucursal
    FROM nuevas n;

    COMMIT;
END;
GO

-- Sucursales de un restaurante para la ficha (Req 11) y el inicio de reserva (Req 14).
-- Zonas (con sus turnos) y preferencias vuelven como JSON, igual que en los servicios de los restaurantes.
CREATE OR ALTER PROCEDURE dbo.get_sucursales_restaurante
    @nro_restaurante INT,
    @cod_idioma      VARCHAR(5) = 'es'
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @nro_idioma INT = COALESCE((SELECT nro_idioma FROM dbo.idiomas WHERE cod_idioma = @cod_idioma), 1);

    SELECT s.nro_restaurante,
           r.razon_social,
           s.nro_sucursal,
           s.nom_sucursal,
           s.calle,
           s.nro_calle,
           s.barrio,
           l.nom_localidad,
           s.cod_postal,
           s.telefonos,
           s.total_comensales,
           s.min_tolerencia_reserva,

           (SELECT z.cod_zona,
                   COALESCE(iz.zona, iz_es.zona, z.desc_zona) AS zona,
                   z.cant_comensales,
                   z.permite_menores,
                   (SELECT CONVERT(VARCHAR(5), zt.hora_desde, 108) AS hora_desde,
                           CONVERT(VARCHAR(5), t.hora_hasta, 108)  AS hora_hasta,
                           zt.permite_menores
                    FROM dbo.zonas_turnos_sucurales_restaurantes zt
                    JOIN dbo.turnos_sucursales_restaurantes t
                      ON t.nro_restaurante = zt.nro_restaurante AND t.nro_sucursal = zt.nro_sucursal
                     AND t.hora_desde = zt.hora_desde
                    WHERE zt.nro_restaurante = z.nro_restaurante AND zt.nro_sucursal = z.nro_sucursal
                      AND zt.cod_zona = z.cod_zona AND t.habilitado = 1
                    ORDER BY zt.hora_desde
                    FOR JSON PATH) AS turnos
            FROM dbo.zonas_sucursales_restaurantes z
            LEFT JOIN dbo.idiomas_zonas_suc_restaurantes iz
              ON iz.nro_restaurante = z.nro_restaurante AND iz.nro_sucursal = z.nro_sucursal
             AND iz.cod_zona = z.cod_zona AND iz.nro_idioma = @nro_idioma
            LEFT JOIN dbo.idiomas_zonas_suc_restaurantes iz_es
              ON iz_es.nro_restaurante = z.nro_restaurante AND iz_es.nro_sucursal = z.nro_sucursal
             AND iz_es.cod_zona = z.cod_zona AND iz_es.nro_idioma = 1
            WHERE z.nro_restaurante = s.nro_restaurante AND z.nro_sucursal = s.nro_sucursal AND z.habilitada = 1
            ORDER BY z.cod_zona
            FOR JSON PATH) AS zonas,

           (SELECT ic.categoria, idc.valor_dominio
            FROM dbo.preferencias_restaurantes p
            JOIN dbo.idiomas_categorias_preferencias ic
              ON ic.cod_categoria = p.cod_categoria AND ic.nro_idioma = @nro_idioma
            JOIN dbo.idiomas_dominio_cat_preferencias idc
              ON idc.cod_categoria = p.cod_categoria AND idc.nro_valor_dominio = p.nro_valor_dominio
             AND idc.nro_idioma = @nro_idioma
            WHERE p.nro_restaurante = s.nro_restaurante AND p.nro_sucursal = s.nro_sucursal
            ORDER BY p.cod_categoria
            FOR JSON PATH) AS preferencias
    FROM dbo.sucursales_restaurantes s
    JOIN dbo.restaurantes r ON r.nro_restaurante = s.nro_restaurante
    JOIN dbo.localidades  l ON l.nro_localidad   = s.nro_localidad
    WHERE s.nro_restaurante = @nro_restaurante
    ORDER BY s.nro_sucursal;
END;
GO

-- Búsqueda estructurada (Req 9 y destino de la búsqueda en lenguaje natural, Req 10/35).
-- @valores_dominio: JSON [{"codCategoria":1,"nroValorDominio":5}, ...]. Devuelve las sucursales
-- ordenadas por cantidad de coincidencias. Sin filtros devuelve todas.
CREATE OR ALTER PROCEDURE dbo.buscar_restaurantes
    @valores_dominio NVARCHAR(MAX) = NULL,
    @barrio          VARCHAR(100)  = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @filtros TABLE (cod_categoria INT, nro_valor_dominio INT);
    INSERT INTO @filtros
    SELECT codCategoria, nroValorDominio
    FROM OPENJSON(COALESCE(@valores_dominio, N'[]')) WITH (codCategoria INT, nroValorDominio INT);

    SELECT s.nro_restaurante,
           r.razon_social,
           s.nro_sucursal,
           s.nom_sucursal,
           s.calle,
           s.nro_calle,
           s.barrio,
           l.nom_localidad,
           (SELECT COUNT(*) FROM dbo.preferencias_restaurantes p
            JOIN @filtros f ON f.cod_categoria = p.cod_categoria AND f.nro_valor_dominio = p.nro_valor_dominio
            WHERE p.nro_restaurante = s.nro_restaurante AND p.nro_sucursal = s.nro_sucursal) AS coincidencias
    FROM dbo.sucursales_restaurantes s
    JOIN dbo.restaurantes r ON r.nro_restaurante = s.nro_restaurante
    JOIN dbo.localidades  l ON l.nro_localidad   = s.nro_localidad
    WHERE (@barrio IS NULL OR s.barrio = @barrio)
      AND (NOT EXISTS (SELECT 1 FROM @filtros)
           OR EXISTS (SELECT 1 FROM dbo.preferencias_restaurantes p
                      JOIN @filtros f ON f.cod_categoria = p.cod_categoria AND f.nro_valor_dominio = p.nro_valor_dominio
                      WHERE p.nro_restaurante = s.nro_restaurante AND p.nro_sucursal = s.nro_sucursal))
    ORDER BY coincidencias DESC, r.razon_social, s.nro_sucursal;
END;
GO

-- ============================================================
-- CONTENIDOS PROMOCIONALES Y CLICS
-- ============================================================

-- Sincronización de un contenido (Req 46): lo llama ContenidoRepository.insUpdContenido con lo que devuelve obtenerContenidos.
-- Se guarda en español; contenido_promocional queda NULL hasta que lo genere la IA (Req 34/48).
CREATE OR ALTER PROCEDURE dbo.ins_upd_contenido_restaurante
    @nro_restaurante            INT,
    @cod_contenido_restaurante  VARCHAR(20),
    @nro_sucursal               INT           = NULL,
    @contenido_a_publicar       VARCHAR(1000),
    @imagen_a_publicar          VARCHAR(255)  = NULL,
    @costo_click                DECIMAL(12,2),
    @dias_vigencia              INT           = 30
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM dbo.contenidos_restaurantes
               WHERE nro_restaurante = @nro_restaurante AND nro_idioma = 1
                 AND cod_contenido_restaurante = @cod_contenido_restaurante)
    BEGIN
        UPDATE dbo.contenidos_restaurantes
        SET nro_sucursal = @nro_sucursal, contenido_a_publicar = @contenido_a_publicar,
            imagen_promocional = @imagen_a_publicar, costo_click = @costo_click
        WHERE nro_restaurante = @nro_restaurante AND nro_idioma = 1
          AND cod_contenido_restaurante = @cod_contenido_restaurante;
        RETURN;
    END;

    INSERT INTO dbo.contenidos_restaurantes (nro_restaurante, nro_idioma, nro_contenido, nro_sucursal,
                                             contenido_promocional, imagen_promocional, contenido_a_publicar,
                                             fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante)
    SELECT @nro_restaurante, 1,
           COALESCE((SELECT MAX(nro_contenido) FROM dbo.contenidos_restaurantes
                     WHERE nro_restaurante = @nro_restaurante AND nro_idioma = 1), 0) + 1,
           @nro_sucursal, NULL, @imagen_a_publicar, @contenido_a_publicar,
           CAST(GETDATE() AS DATE), DATEADD(DAY, @dias_vigencia, CAST(GETDATE() AS DATE)),
           @costo_click, @cod_contenido_restaurante;
END;
GO

-- Contenidos vigentes para la portada y la ficha del restaurante (Req 12).
-- Muestra el texto generado por IA si existe; si no, el que envió el restaurante.
-- Si no hay versión en el idioma pedido, usa la de español.
CREATE OR ALTER PROCEDURE dbo.get_contenidos_vigentes
    @cod_idioma      VARCHAR(5) = 'es',
    @nro_restaurante INT        = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @nro_idioma INT = COALESCE((SELECT nro_idioma FROM dbo.idiomas WHERE cod_idioma = @cod_idioma), 1);
    DECLARE @hoy DATE = CAST(GETDATE() AS DATE);

    SELECT c.nro_restaurante,
           c.nro_idioma,
           c.nro_contenido,
           c.nro_sucursal,
           r.razon_social,
           s.nom_sucursal,
           COALESCE(c.contenido_promocional, c.contenido_a_publicar) AS contenido_promocional,
           c.imagen_promocional,
           CONVERT(VARCHAR(10), c.fecha_ini_vigencia, 23) AS fecha_ini_vigencia,
           CONVERT(VARCHAR(10), c.fecha_fin_vigencia, 23) AS fecha_fin_vigencia,
           c.costo_click,
           c.cod_contenido_restaurante
    FROM dbo.contenidos_restaurantes c
    JOIN dbo.restaurantes r ON r.nro_restaurante = c.nro_restaurante
    LEFT JOIN dbo.sucursales_restaurantes s ON s.nro_restaurante = c.nro_restaurante AND s.nro_sucursal = c.nro_sucursal
    WHERE @hoy BETWEEN c.fecha_ini_vigencia AND c.fecha_fin_vigencia
      AND (@nro_restaurante IS NULL OR c.nro_restaurante = @nro_restaurante)
      AND (c.nro_idioma = @nro_idioma
           OR (c.nro_idioma = 1 AND NOT EXISTS (SELECT 1 FROM dbo.contenidos_restaurantes c2
                                                WHERE c2.nro_restaurante = c.nro_restaurante
                                                  AND c2.cod_contenido_restaurante = c.cod_contenido_restaurante
                                                  AND c2.nro_idioma = @nro_idioma)))
    ORDER BY c.fecha_fin_vigencia, r.razon_social;
END;
GO

-- Contenidos que todavía no tienen texto generado por IA (insumo del Req 34)
CREATE OR ALTER PROCEDURE dbo.get_contenidos_sin_generar
AS
BEGIN
    SET NOCOUNT ON;
    SELECT c.nro_restaurante, c.nro_idioma, c.nro_contenido, c.nro_sucursal, r.razon_social,
           c.contenido_a_publicar, c.imagen_promocional, c.costo_click, c.cod_contenido_restaurante
    FROM dbo.contenidos_restaurantes c
    JOIN dbo.restaurantes r ON r.nro_restaurante = c.nro_restaurante
    WHERE c.contenido_promocional IS NULL;
END;
GO

-- Registra el texto generado por IA (Req 48)
CREATE OR ALTER PROCEDURE dbo.upd_contenido_promocional
    @nro_restaurante        INT,
    @nro_idioma             INT,
    @nro_contenido          INT,
    @contenido_promocional  VARCHAR(2000)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.contenidos_restaurantes
    SET contenido_promocional = @contenido_promocional
    WHERE nro_restaurante = @nro_restaurante AND nro_idioma = @nro_idioma AND nro_contenido = @nro_contenido;
END;
GO

-- Clic en un contenido (Req 50). El costo es el del contenido. @nro_cliente NULL = visitante.
CREATE OR ALTER PROCEDURE dbo.ins_click_contenido
    @nro_restaurante  INT,
    @nro_idioma       INT,
    @nro_contenido    INT,
    @nro_cliente      INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.clicks_contenidos_restaurantes (nro_restaurante, nro_idioma, nro_contenido, nro_cliente, costo_click)
    SELECT c.nro_restaurante, c.nro_idioma, c.nro_contenido, @nro_cliente, c.costo_click
    FROM dbo.contenidos_restaurantes c
    WHERE c.nro_restaurante = @nro_restaurante AND c.nro_idioma = @nro_idioma AND c.nro_contenido = @nro_contenido;

    SELECT CAST(SCOPE_IDENTITY() AS INT) AS nro_click;
END;
GO

-- Clics todavía no informados al restaurante (los notifica el batch / AdminTask)
CREATE OR ALTER PROCEDURE dbo.get_clicks_pendientes
    @nro_restaurante INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT k.nro_restaurante, k.nro_idioma, k.nro_contenido, k.nro_click,
           c.cod_contenido_restaurante,
           CONVERT(VARCHAR(19), k.fecha_hora_registro, 126) AS fecha_hora_registro,
           k.costo_click,
           cl.apellido, cl.nombre, cl.correo, cl.telefonos
    FROM dbo.clicks_contenidos_restaurantes k
    JOIN dbo.contenidos_restaurantes c
      ON c.nro_restaurante = k.nro_restaurante AND c.nro_idioma = k.nro_idioma AND c.nro_contenido = k.nro_contenido
    LEFT JOIN dbo.clientes cl ON cl.nro_cliente = k.nro_cliente
    WHERE k.nro_restaurante = @nro_restaurante AND k.notificado = 0
    ORDER BY k.fecha_hora_registro;
END;
GO

CREATE OR ALTER PROCEDURE dbo.upd_click_notificado
    @nro_restaurante  INT,
    @nro_idioma       INT,
    @nro_contenido    INT,
    @nro_click        INT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.clicks_contenidos_restaurantes
    SET notificado = 1
    WHERE nro_restaurante = @nro_restaurante AND nro_idioma = @nro_idioma
      AND nro_contenido = @nro_contenido AND nro_click = @nro_click;
END;
GO

CREATE OR ALTER PROCEDURE dbo.upd_descripcion_restaurante
    @nro_restaurante  INT,
    @descripcion      VARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;                              -- no devuelve el "1 row affected"
    UPDATE dbo.restaurantes                      -- la TABLA que se modifica
    SET descripcion = @descripcion               -- la COLUMNA = el VALOR que llega por parámetro
    WHERE nro_restaurante = @nro_restaurante;    -- solo la fila de ESE restaurante
END;
GO

-- ============================================================
-- CLIENTES (registro y login)
-- ============================================================

-- Alta de cliente (Req 1). @clave llega ya hasheada (BCrypt) desde el backend.
-- @preferencias: JSON [{"codCategoria":1,"nroValorDominio":5,"observaciones":null}, ...]
CREATE OR ALTER PROCEDURE dbo.ins_cliente
    @apellido       VARCHAR(100),
    @nombre         VARCHAR(100),
    @clave          VARCHAR(100),
    @correo         VARCHAR(150),
    @telefonos      VARCHAR(100)  = NULL,
    @nro_localidad  INT           = NULL,
    @preferencias   NVARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF EXISTS (SELECT 1 FROM dbo.clientes WHERE correo = @correo)
    BEGIN
        SELECT CAST(NULL AS INT) AS nro_cliente, 'RECHAZADO' AS estado, 'CORREO_EXISTENTE' AS motivo;
        RETURN;
    END;

    DECLARE @nro_cliente INT;

    BEGIN TRAN;

    INSERT INTO dbo.clientes (apellido, nombre, clave, correo, telefonos, nro_localidad)
    VALUES (@apellido, @nombre, @clave, @correo, @telefonos, @nro_localidad);
    SET @nro_cliente = SCOPE_IDENTITY();

    INSERT INTO dbo.preferencias_clientes (nro_cliente, cod_categoria, nro_valor_dominio, observaciones)
    SELECT DISTINCT @nro_cliente, codCategoria, nroValorDominio, observaciones
    FROM OPENJSON(COALESCE(@preferencias, N'[]'))
         WITH (codCategoria INT, nroValorDominio INT, observaciones VARCHAR(255));

    COMMIT;

    SELECT @nro_cliente AS nro_cliente, 'REGISTRADO' AS estado, CAST(NULL AS VARCHAR(50)) AS motivo;
END;
GO

-- Para el login (Req 2): devuelve la clave hasheada para que el backend la verifique
CREATE OR ALTER PROCEDURE dbo.get_cliente_por_correo
    @correo VARCHAR(150)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT nro_cliente, apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado
    FROM dbo.clientes
    WHERE correo = @correo;
END;
GO

CREATE OR ALTER PROCEDURE dbo.get_cliente
    @nro_cliente INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT nro_cliente, apellido, nombre, correo, telefonos, nro_localidad, habilitado
    FROM dbo.clientes
    WHERE nro_cliente = @nro_cliente;
END;
GO

-- ============================================================
-- CATÁLOGOS
-- ============================================================

-- Valores de preferencias en el idioma pedido (formulario de registro y filtros de búsqueda)
CREATE OR ALTER PROCEDURE dbo.get_preferencias
    @cod_idioma VARCHAR(5) = 'es'
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @nro_idioma INT = COALESCE((SELECT nro_idioma FROM dbo.idiomas WHERE cod_idioma = @cod_idioma), 1);

    SELECT d.cod_categoria, ic.categoria, d.nro_valor_dominio, idc.valor_dominio
    FROM dbo.dominio_categorias_preferencias d
    JOIN dbo.idiomas_categorias_preferencias ic
      ON ic.cod_categoria = d.cod_categoria AND ic.nro_idioma = @nro_idioma
    JOIN dbo.idiomas_dominio_cat_preferencias idc
      ON idc.cod_categoria = d.cod_categoria AND idc.nro_valor_dominio = d.nro_valor_dominio AND idc.nro_idioma = @nro_idioma
    ORDER BY d.cod_categoria, d.nro_valor_dominio;
END;
GO

CREATE OR ALTER PROCEDURE dbo.get_localidades
AS
BEGIN
    SET NOCOUNT ON;
    SELECT l.nro_localidad, l.nom_localidad, p.cod_provincia, p.nom_provincia
    FROM dbo.localidades l
    JOIN dbo.provincias p ON p.cod_provincia = l.cod_provincia
    ORDER BY p.nom_provincia, l.nom_localidad;
END;
GO

CREATE OR ALTER PROCEDURE dbo.get_estados_reservas
    @cod_idioma VARCHAR(5) = 'es'
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @nro_idioma INT = COALESCE((SELECT nro_idioma FROM dbo.idiomas WHERE cod_idioma = @cod_idioma), 1);
    SELECT e.cod_estado, ie.estado
    FROM dbo.estados_reservas e
    JOIN dbo.idiomas_estados ie ON ie.cod_estado = e.cod_estado AND ie.nro_idioma = @nro_idioma
    ORDER BY e.cod_estado;
END;
GO

-- ============================================================
-- RESERVAS
-- ============================================================

-- Fee vigente hoy para un tipo de costo (RESERVA | CLICK)
CREATE OR ALTER PROCEDURE dbo.get_costo_vigente
    @tipo_costo VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP 1 tipo_costo, monto
    FROM dbo.costos
    WHERE tipo_costo = @tipo_costo
      AND CAST(GETDATE() AS DATE) >= fecha_ini_vigencia
      AND (fecha_fin_vigencia IS NULL OR CAST(GETDATE() AS DATE) <= fecha_fin_vigencia)
    ORDER BY fecha_ini_vigencia DESC;
END;
GO

-- Registra en el portal una reserva que el restaurante ya confirmó (Req 15/38).
-- Guarda el código del restaurante y el fee para la liquidación mensual.
CREATE OR ALTER PROCEDURE dbo.ins_reserva_restaurante
    @nro_cliente           INT,
    @nro_restaurante       INT,
    @nro_sucursal          INT,
    @cod_zona              INT,
    @fecha_reserva         DATE,
    @hora_reserva          TIME,
    @cant_adultos          INT,
    @cant_menores          INT,
    @costo_reserva         DECIMAL(12,2),
    @cod_reserva_sucursal  VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.reservas_restaurantes (nro_cliente, fecha_reserva, nro_restaurante, nro_sucursal, cod_zona,
                                           hora_reserva, cant_adultos, cant_menores, cod_estado, costo_reserva,
                                           cod_reserva_sucursal)
    VALUES (@nro_cliente, @fecha_reserva, @nro_restaurante, @nro_sucursal, @cod_zona,
            @hora_reserva, @cant_adultos, COALESCE(@cant_menores, 0), 1, @costo_reserva,
            @cod_reserva_sucursal);

    SELECT CAST(SCOPE_IDENTITY() AS INT) AS nro_reserva;
END;
GO

-- Reservas del cliente con filtros (Req 16)
CREATE OR ALTER PROCEDURE dbo.get_reservas_cliente
    @nro_cliente  INT,
    @cod_idioma   VARCHAR(5) = 'es',
    @cod_estado   INT        = NULL,
    @fecha_desde  DATE       = NULL,
    @fecha_hasta  DATE       = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @nro_idioma INT = COALESCE((SELECT nro_idioma FROM dbo.idiomas WHERE cod_idioma = @cod_idioma), 1);

    SELECT rr.nro_reserva,
           rr.cod_reserva_sucursal,
           r.razon_social,
           s.nom_sucursal,
           s.calle,
           s.nro_calle,
           s.barrio,
           CONVERT(VARCHAR(10), rr.fecha_reserva, 23) AS fecha_reserva,
           CONVERT(VARCHAR(5), rr.hora_reserva, 108)  AS hora_reserva,
           rr.cod_zona,
           COALESCE(iz.zona, z.desc_zona) AS zona,
           rr.cant_adultos,
           rr.cant_menores,
           rr.cod_estado,
           ie.estado,
           rr.costo_reserva
    FROM dbo.reservas_restaurantes rr
    JOIN dbo.restaurantes r ON r.nro_restaurante = rr.nro_restaurante
    JOIN dbo.sucursales_restaurantes s ON s.nro_restaurante = rr.nro_restaurante AND s.nro_sucursal = rr.nro_sucursal
    JOIN dbo.zonas_sucursales_restaurantes z
      ON z.nro_restaurante = rr.nro_restaurante AND z.nro_sucursal = rr.nro_sucursal AND z.cod_zona = rr.cod_zona
    LEFT JOIN dbo.idiomas_zonas_suc_restaurantes iz
      ON iz.nro_restaurante = z.nro_restaurante AND iz.nro_sucursal = z.nro_sucursal
     AND iz.cod_zona = z.cod_zona AND iz.nro_idioma = @nro_idioma
    JOIN dbo.idiomas_estados ie ON ie.cod_estado = rr.cod_estado AND ie.nro_idioma = @nro_idioma
    WHERE rr.nro_cliente = @nro_cliente
      AND (@cod_estado  IS NULL OR rr.cod_estado = @cod_estado)
      AND (@fecha_desde IS NULL OR rr.fecha_reserva >= @fecha_desde)
      AND (@fecha_hasta IS NULL OR rr.fecha_reserva <= @fecha_hasta)
    ORDER BY rr.fecha_reserva DESC, rr.hora_reserva DESC;
END;
GO
