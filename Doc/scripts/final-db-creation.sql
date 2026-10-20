-- ============================================================
-- BD: ristorino  |  Motor: SQL Server 2019+
-- Fuente: RISTORINO-Logico.pdf (nombres de tablas y columnas respetados
-- tal cual, incluidos "min_tolerencia_reserva" y "zonas_turnos_sucurales_restaurantes").
-- Script idempotente: se puede volver a ejecutar para dejar la base en su estado inicial.
-- ============================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ristorino')
BEGIN
    CREATE DATABASE ristorino COLLATE Modern_Spanish_CI_AS;
END
GO

USE ristorino;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- ============================================================
-- LIMPIEZA (orden inverso de FK)
-- ============================================================
DROP TABLE IF EXISTS dbo.preferencias_reservas_restaurantes;
DROP TABLE IF EXISTS dbo.reservas_restaurantes;
DROP TABLE IF EXISTS dbo.idiomas_estados;
DROP TABLE IF EXISTS dbo.estados_reservas;
DROP TABLE IF EXISTS dbo.clicks_contenidos_restaurantes;
DROP TABLE IF EXISTS dbo.contenidos_restaurantes;
DROP TABLE IF EXISTS dbo.preferencias_clientes;
DROP TABLE IF EXISTS dbo.clientes;
DROP TABLE IF EXISTS dbo.preferencias_restaurantes;
DROP TABLE IF EXISTS dbo.idiomas_dominio_cat_preferencias;
DROP TABLE IF EXISTS dbo.idiomas_categorias_preferencias;
DROP TABLE IF EXISTS dbo.dominio_categorias_preferencias;
DROP TABLE IF EXISTS dbo.categorias_preferencias;
DROP TABLE IF EXISTS dbo.idiomas_zonas_suc_restaurantes;
DROP TABLE IF EXISTS dbo.zonas_turnos_sucurales_restaurantes;
DROP TABLE IF EXISTS dbo.zonas_sucursales_restaurantes;
DROP TABLE IF EXISTS dbo.turnos_sucursales_restaurantes;
DROP TABLE IF EXISTS dbo.sucursales_restaurantes;
DROP TABLE IF EXISTS dbo.configuracion_restaurantes;
DROP TABLE IF EXISTS dbo.atributos;
DROP TABLE IF EXISTS dbo.restaurantes;
DROP TABLE IF EXISTS dbo.costos;
DROP TABLE IF EXISTS dbo.idiomas;
DROP TABLE IF EXISTS dbo.localidades;
DROP TABLE IF EXISTS dbo.provincias;
GO

-- ============================================================
-- TABLAS
-- ============================================================

CREATE TABLE dbo.provincias (
    cod_provincia  INT          NOT NULL PRIMARY KEY,
    nom_provincia  VARCHAR(100) NOT NULL
);

CREATE TABLE dbo.localidades (
    nro_localidad  INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nom_localidad  VARCHAR(100) NOT NULL,
    cod_provincia  INT          NOT NULL REFERENCES dbo.provincias (cod_provincia),
    CONSTRAINT AK_localidades UNIQUE (cod_provincia, nom_localidad)
);

CREATE TABLE dbo.idiomas (
    nro_idioma  INT         NOT NULL PRIMARY KEY,
    nom_idioma  VARCHAR(50) NOT NULL,
    cod_idioma  VARCHAR(5)  NOT NULL UNIQUE           -- es | en
);

-- Fees vigentes (RESERVA, CLICK) para la liquidación mensual
CREATE TABLE dbo.costos (
    tipo_costo          VARCHAR(20)    NOT NULL,
    fecha_ini_vigencia  DATE           NOT NULL,
    fecha_fin_vigencia  DATE           NULL,
    monto               DECIMAL(12,2)  NOT NULL,
    CONSTRAINT PK_costos PRIMARY KEY (tipo_costo, fecha_ini_vigencia)
);

CREATE TABLE dbo.restaurantes (
    nro_restaurante  INT          NOT NULL PRIMARY KEY,
    razon_social     VARCHAR(100) NOT NULL,
    cuit             VARCHAR(13)  NOT NULL,
    CONSTRAINT AK_restaurantes_cuit UNIQUE (cuit)
);

-- Atributos de configuración de la integración (URL, protocolo, credenciales...)
CREATE TABLE dbo.atributos (
    cod_atributo  VARCHAR(30)  NOT NULL PRIMARY KEY,
    nom_atributo  VARCHAR(100) NOT NULL,
    tipo_dato     VARCHAR(20)  NOT NULL
);

CREATE TABLE dbo.configuracion_restaurantes (
    nro_restaurante  INT          NOT NULL REFERENCES dbo.restaurantes (nro_restaurante),
    cod_atributo     VARCHAR(30)  NOT NULL REFERENCES dbo.atributos (cod_atributo),
    valor            VARCHAR(500) NOT NULL,
    CONSTRAINT PK_configuracion_restaurantes PRIMARY KEY (nro_restaurante, cod_atributo)
);

CREATE TABLE dbo.sucursales_restaurantes (
    nro_restaurante           INT           NOT NULL REFERENCES dbo.restaurantes (nro_restaurante),
    nro_sucursal              INT           NOT NULL,
    nom_sucursal              VARCHAR(100)  NOT NULL,
    calle                     VARCHAR(100)  NOT NULL,
    nro_calle                 INT           NOT NULL,
    barrio                    VARCHAR(100)  NOT NULL,
    nro_localidad             INT           NOT NULL REFERENCES dbo.localidades (nro_localidad),
    cod_postal                VARCHAR(10)   NULL,
    telefonos                 VARCHAR(100)  NULL,
    total_comensales          INT           NOT NULL,
    min_tolerencia_reserva    INT           NOT NULL,
    cod_sucursal_restaurante  VARCHAR(20)   NOT NULL,   -- código de la sucursal en el sistema del restaurante
    CONSTRAINT PK_sucursales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal),
    CONSTRAINT AK_sucursales_restaurantes UNIQUE (nro_restaurante, cod_sucursal_restaurante)
);

CREATE TABLE dbo.turnos_sucursales_restaurantes (
    nro_restaurante  INT  NOT NULL,
    nro_sucursal     INT  NOT NULL,
    hora_desde       TIME NOT NULL,
    hora_hasta       TIME NOT NULL,
    habilitado       BIT  NOT NULL DEFAULT 1,
    CONSTRAINT PK_turnos_sucursales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal, hora_desde),
    CONSTRAINT FK_turnos_sucursales_restaurantes FOREIGN KEY (nro_restaurante, nro_sucursal)
        REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);

CREATE TABLE dbo.zonas_sucursales_restaurantes (
    nro_restaurante  INT          NOT NULL,
    nro_sucursal     INT          NOT NULL,
    cod_zona         INT          NOT NULL,
    desc_zona        VARCHAR(100) NOT NULL,
    cant_comensales  INT          NOT NULL,
    permite_menores  BIT          NOT NULL DEFAULT 1,
    habilitada       BIT          NOT NULL DEFAULT 1,
    CONSTRAINT PK_zonas_sucursales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_zonas_sucursales_restaurantes FOREIGN KEY (nro_restaurante, nro_sucursal)
        REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);

-- Nombre de la tabla respetado tal cual el modelo lógico ("sucurales")
CREATE TABLE dbo.zonas_turnos_sucurales_restaurantes (
    nro_restaurante  INT  NOT NULL,
    nro_sucursal     INT  NOT NULL,
    cod_zona         INT  NOT NULL,
    hora_desde       TIME NOT NULL,
    permite_menores  BIT  NOT NULL DEFAULT 1,
    CONSTRAINT PK_zonas_turnos_sucurales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, hora_desde),
    CONSTRAINT FK_zonas_turnos_zonas FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
        REFERENCES dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_zonas_turnos_turnos FOREIGN KEY (nro_restaurante, nro_sucursal, hora_desde)
        REFERENCES dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde)
);

CREATE TABLE dbo.idiomas_zonas_suc_restaurantes (
    nro_restaurante  INT          NOT NULL,
    nro_sucursal     INT          NOT NULL,
    cod_zona         INT          NOT NULL,
    nro_idioma       INT          NOT NULL REFERENCES dbo.idiomas (nro_idioma),
    zona             VARCHAR(100) NOT NULL,
    desc_zona        VARCHAR(255) NULL,
    CONSTRAINT PK_idiomas_zonas_suc_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, nro_idioma),
    CONSTRAINT FK_idiomas_zonas_suc_restaurantes FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
        REFERENCES dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal, cod_zona)
);

-- Preferencias gastronómicas: categorías (tipo de cocina, especialidad, estilo, nivel de precio) y sus valores
CREATE TABLE dbo.categorias_preferencias (
    cod_categoria  INT          NOT NULL PRIMARY KEY,
    nom_categoria  VARCHAR(100) NOT NULL
);

CREATE TABLE dbo.dominio_categorias_preferencias (
    cod_categoria      INT          NOT NULL REFERENCES dbo.categorias_preferencias (cod_categoria),
    nro_valor_dominio  INT          NOT NULL,
    nom_valor_dominio  VARCHAR(100) NOT NULL,
    CONSTRAINT PK_dominio_categorias_preferencias PRIMARY KEY (cod_categoria, nro_valor_dominio)
);

CREATE TABLE dbo.idiomas_categorias_preferencias (
    cod_categoria   INT          NOT NULL REFERENCES dbo.categorias_preferencias (cod_categoria),
    nro_idioma      INT          NOT NULL REFERENCES dbo.idiomas (nro_idioma),
    categoria       VARCHAR(100) NOT NULL,
    desc_categoria  VARCHAR(255) NULL,
    CONSTRAINT PK_idiomas_categorias_preferencias PRIMARY KEY (cod_categoria, nro_idioma)
);

CREATE TABLE dbo.idiomas_dominio_cat_preferencias (
    cod_categoria       INT          NOT NULL,
    nro_valor_dominio   INT          NOT NULL,
    nro_idioma          INT          NOT NULL REFERENCES dbo.idiomas (nro_idioma),
    valor_dominio       VARCHAR(100) NOT NULL,
    desc_valor_dominio  VARCHAR(255) NULL,
    CONSTRAINT PK_idiomas_dominio_cat_preferencias PRIMARY KEY (cod_categoria, nro_valor_dominio, nro_idioma),
    CONSTRAINT FK_idiomas_dominio_cat_preferencias FOREIGN KEY (cod_categoria, nro_valor_dominio)
        REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio)
);

CREATE TABLE dbo.preferencias_restaurantes (
    nro_restaurante    INT          NOT NULL REFERENCES dbo.restaurantes (nro_restaurante),
    cod_categoria      INT          NOT NULL,
    nro_valor_dominio  INT          NOT NULL,
    nro_preferencia    INT          NOT NULL,
    observaciones      VARCHAR(255) NULL,
    nro_sucursal       INT          NULL,
    CONSTRAINT PK_preferencias_restaurantes PRIMARY KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
    CONSTRAINT FK_preferencias_restaurantes_dominio FOREIGN KEY (cod_categoria, nro_valor_dominio)
        REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_preferencias_restaurantes_sucursal FOREIGN KEY (nro_restaurante, nro_sucursal)
        REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);

CREATE TABLE dbo.clientes (
    nro_cliente    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    apellido       VARCHAR(100) NOT NULL,
    nombre         VARCHAR(100) NOT NULL,
    clave          VARCHAR(100) NOT NULL,     -- hash BCrypt, nunca la contraseña en texto plano
    correo         VARCHAR(150) NOT NULL,
    telefonos      VARCHAR(100) NULL,
    nro_localidad  INT          NULL REFERENCES dbo.localidades (nro_localidad),
    habilitado     BIT          NOT NULL DEFAULT 1,
    CONSTRAINT AK_clientes_correo UNIQUE (correo)
);

CREATE TABLE dbo.preferencias_clientes (
    nro_cliente        INT          NOT NULL REFERENCES dbo.clientes (nro_cliente),
    cod_categoria      INT          NOT NULL,
    nro_valor_dominio  INT          NOT NULL,
    observaciones      VARCHAR(255) NULL,
    CONSTRAINT PK_preferencias_clientes PRIMARY KEY (nro_cliente, cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_preferencias_clientes_dominio FOREIGN KEY (cod_categoria, nro_valor_dominio)
        REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio)
);

-- Contenidos promocionales por restaurante e idioma.
-- contenido_a_publicar = lo que envía el restaurante; contenido_promocional = lo que genera la IA.
CREATE TABLE dbo.contenidos_restaurantes (
    nro_restaurante            INT            NOT NULL REFERENCES dbo.restaurantes (nro_restaurante),
    nro_idioma                 INT            NOT NULL REFERENCES dbo.idiomas (nro_idioma),
    nro_contenido              INT            NOT NULL,
    nro_sucursal               INT            NULL,
    contenido_promocional      VARCHAR(2000)  NULL,
    imagen_promocional         VARCHAR(255)   NULL,
    contenido_a_publicar       VARCHAR(1000)  NOT NULL,
    fecha_ini_vigencia         DATE           NOT NULL,
    fecha_fin_vigencia         DATE           NOT NULL,
    costo_click                DECIMAL(12,2)  NOT NULL,
    cod_contenido_restaurante  VARCHAR(20)    NOT NULL,
    CONSTRAINT PK_contenidos_restaurantes PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido),
    CONSTRAINT FK_contenidos_restaurantes_sucursal FOREIGN KEY (nro_restaurante, nro_sucursal)
        REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);

CREATE TABLE dbo.clicks_contenidos_restaurantes (
    nro_restaurante      INT            NOT NULL,
    nro_idioma           INT            NOT NULL,
    nro_contenido        INT            NOT NULL,
    nro_click            INT IDENTITY(1,1) NOT NULL,
    fecha_hora_registro  DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
    nro_cliente          INT            NULL REFERENCES dbo.clientes (nro_cliente),
    costo_click          DECIMAL(12,2)  NOT NULL,
    notificado           BIT            NOT NULL DEFAULT 0,   -- 1 = ya informado al restaurante
    CONSTRAINT PK_clicks_contenidos_restaurantes PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido, nro_click),
    CONSTRAINT FK_clicks_contenidos_restaurantes FOREIGN KEY (nro_restaurante, nro_idioma, nro_contenido)
        REFERENCES dbo.contenidos_restaurantes (nro_restaurante, nro_idioma, nro_contenido)
);

CREATE TABLE dbo.estados_reservas (
    cod_estado  INT         NOT NULL PRIMARY KEY,
    nom_estado  VARCHAR(50) NOT NULL
);

CREATE TABLE dbo.idiomas_estados (
    cod_estado  INT         NOT NULL REFERENCES dbo.estados_reservas (cod_estado),
    nro_idioma  INT         NOT NULL REFERENCES dbo.idiomas (nro_idioma),
    estado      VARCHAR(50) NOT NULL,
    CONSTRAINT PK_idiomas_estados PRIMARY KEY (cod_estado, nro_idioma)
);

CREATE TABLE dbo.reservas_restaurantes (
    nro_cliente           INT            NOT NULL REFERENCES dbo.clientes (nro_cliente),
    nro_reserva           INT IDENTITY(1,1) NOT NULL,
    fecha_hora_registro   DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
    fecha_reserva         DATE           NOT NULL,
    nro_restaurante       INT            NOT NULL,
    nro_sucursal          INT            NOT NULL,
    cod_zona              INT            NOT NULL,
    hora_reserva          TIME           NOT NULL,
    cant_adultos          INT            NOT NULL,
    cant_menores          INT            NOT NULL DEFAULT 0,
    cod_estado            INT            NOT NULL REFERENCES dbo.estados_reservas (cod_estado),
    fecha_cancelacion     DATETIME2      NULL,
    costo_reserva         DECIMAL(12,2)  NOT NULL,
    cod_reserva_sucursal  VARCHAR(20)    NOT NULL,   -- código que devolvió el restaurante
    CONSTRAINT PK_reservas_restaurantes PRIMARY KEY (nro_cliente, nro_reserva),
    CONSTRAINT AK_reservas_restaurantes UNIQUE (cod_reserva_sucursal),
    CONSTRAINT FK_reservas_zonas_turnos FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona, hora_reserva)
        REFERENCES dbo.zonas_turnos_sucurales_restaurantes (nro_restaurante, nro_sucursal, cod_zona, hora_desde)
);

CREATE TABLE dbo.preferencias_reservas_restaurantes (
    nro_cliente        INT          NOT NULL,
    nro_reserva        INT          NOT NULL,
    nro_restaurante    INT          NOT NULL,
    cod_categoria      INT          NOT NULL,
    nro_valor_dominio  INT          NOT NULL,
    nro_preferencia    INT          NOT NULL,
    observaciones      VARCHAR(255) NULL,
    CONSTRAINT PK_preferencias_reservas_restaurantes
        PRIMARY KEY (nro_cliente, nro_reserva, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
    CONSTRAINT FK_preferencias_reservas_reserva FOREIGN KEY (nro_cliente, nro_reserva)
        REFERENCES dbo.reservas_restaurantes (nro_cliente, nro_reserva),
    CONSTRAINT FK_preferencias_reservas_preferencia FOREIGN KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
        REFERENCES dbo.preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
);
GO

-- ============================================================
-- DATOS INICIALES
-- ============================================================

INSERT INTO dbo.provincias (cod_provincia, nom_provincia) VALUES (1, 'Córdoba');
INSERT INTO dbo.localidades (nom_localidad, cod_provincia) VALUES ('Córdoba', 1);

INSERT INTO dbo.idiomas (nro_idioma, nom_idioma, cod_idioma)
VALUES (1, 'Español', 'es'), (2, 'English', 'en');

INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES ('RESERVA', '2025-01-01', NULL, 500.00),
       ('CLICK',   '2025-01-01', NULL, 100.00);

-- Restaurantes integrados (los 4 servicios de servicios-restaurantes/)
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit)
VALUES (1, 'La Bella Pizza',    '30-71234567-1'),
       (2, 'Perukai',           '30-71234567-2'),
       (3, 'La Fábrica Burger', '30-71234567-3'),
       (4, 'Sabores del Norte', '30-71234567-4');

INSERT INTO dbo.atributos (cod_atributo, nom_atributo, tipo_dato)
VALUES ('TIPO_SERVICIO', 'Protocolo del servicio (REST | SOAP)', 'STRING'),
       ('URL_SERVICIO',  'URL base (REST) o URL del WSDL (SOAP)', 'STRING'),
       ('USUARIO',       'Usuario para autenticarse en el servicio SOAP (WS-Security)', 'STRING'),
       ('PASSWORD',      'Contraseña para autenticarse en el servicio SOAP (WS-Security)', 'STRING'),
       ('TOKEN',         'Token JWT (Bearer) para autenticarse en el servicio REST', 'STRING'),
       ('NAMESPACE',     'Namespace del servicio SOAP', 'STRING'),
       ('SERVICE_NAME',  'Nombre del service en el WSDL', 'STRING'),
       ('PORT_NAME',     'Nombre del port en el WSDL', 'STRING');

INSERT INTO dbo.configuracion_restaurantes (nro_restaurante, cod_atributo, valor)
VALUES (1, 'TIPO_SERVICIO', 'REST'),
       (1, 'URL_SERVICIO',  'http://localhost:8081/api/v1'),
       (1, 'TOKEN',         'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyaXN0b3Jpbm8iLCJuYW1lIjoiUG9ydGFsIFJpc3RvcmlubyJ9.7TV_uz2FraDgosGOJj38Wd3_HuMSS4PX_1Z5umMIDd4'),
       (2, 'TIPO_SERVICIO', 'SOAP'),
       (2, 'URL_SERVICIO',  'http://localhost:8082/services/perukai.wsdl'),
       (2, 'USUARIO',       'usr_admin'),
       (2, 'PASSWORD',      'pwd_admin'),
       (2, 'NAMESPACE',     'http://services.perukai.das.ubp.edu.ar/'),
       (2, 'SERVICE_NAME',  'PerukaiWSPortService'),
       (2, 'PORT_NAME',     'PerukaiWSPortSoap11'),
       (3, 'TIPO_SERVICIO', 'REST'),
       (3, 'URL_SERVICIO',  'http://localhost:8083/api/v1'),
       (3, 'TOKEN',         'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyaXN0b3Jpbm8iLCJuYW1lIjoiUG9ydGFsIFJpc3RvcmlubyJ9.LD_toshKhCnHzDsN7NOEoEl3CrHoYBBFfzPWuk_QqTk'),
       (4, 'TIPO_SERVICIO', 'SOAP'),
       (4, 'URL_SERVICIO',  'http://localhost:8084/services/saboresnorte.wsdl'),
       (4, 'USUARIO',       'usr_admin'),
       (4, 'PASSWORD',      'pwd_admin'),
       (4, 'NAMESPACE',     'http://services.saboresnorte.das.ubp.edu.ar/'),
       (4, 'SERVICE_NAME',  'SaboresNorteWSPortService'),
       (4, 'PORT_NAME',     'SaboresNorteWSPortSoap11');

-- Categorías de preferencias y sus valores (mismos nombres que los catálogos de los restaurantes,
-- así la sincronización de sucursales los vincula por nombre)
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria)
VALUES (1, 'Tipo de cocina'), (2, 'Especialidades alimentarias'), (3, 'Estilo'), (4, 'Nivel de precio');

INSERT INTO dbo.idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria)
VALUES (1, 1, 'Tipo de cocina'), (1, 2, 'Cuisine'),
       (2, 1, 'Especialidades alimentarias'), (2, 2, 'Dietary options'),
       (3, 1, 'Estilo'), (3, 2, 'Style'),
       (4, 1, 'Nivel de precio'), (4, 2, 'Price range');

INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio)
VALUES (1, 1, 'Italiana'), (1, 2, 'Mexicana'), (1, 3, 'Española'), (1, 4, 'Francesa'), (1, 5, 'Japonesa'),
       (1, 6, 'China'), (1, 7, 'Tailandesa'), (1, 8, 'India'), (1, 9, 'Mediterránea'), (1, 10, 'Argentina'),
       (1, 11, 'Peruana'), (1, 12, 'Árabe / Medio Oriente'), (1, 13, 'Americana'), (1, 14, 'Fusión'), (1, 15, 'Internacional'),
       (2, 1, 'Vegetariana'), (2, 2, 'Vegana'), (2, 3, 'Sin gluten / Celíaco'), (2, 4, 'Sin lactosa'),
       (2, 5, 'Baja en calorías'), (2, 6, 'Orgánica'), (2, 7, 'Diabéticos (sin azúcar añadida)'),
       (3, 1, 'Gourmet'), (3, 2, 'Casual'), (3, 3, 'Comida rápida / Fast food'), (3, 4, 'Buffet libre'),
       (3, 5, 'Bistró'), (3, 6, 'Food truck'), (3, 7, 'Restaurante tradicional'), (3, 8, 'Bar / Tapas'),
       (3, 9, 'Cafetería'), (3, 10, 'Delivery'), (3, 11, 'Fine dining'),
       (4, 1, 'Económico / Bajo'), (4, 2, 'Medio'), (4, 3, 'Alto / Premium'), (4, 4, 'De lujo');

-- Español = nombre del dominio; inglés = traducción
INSERT INTO dbo.idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
SELECT cod_categoria, nro_valor_dominio, 1, nom_valor_dominio FROM dbo.dominio_categorias_preferencias;

INSERT INTO dbo.idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio)
VALUES (1, 1, 2, 'Italian'), (1, 2, 2, 'Mexican'), (1, 3, 2, 'Spanish'), (1, 4, 2, 'French'), (1, 5, 2, 'Japanese'),
       (1, 6, 2, 'Chinese'), (1, 7, 2, 'Thai'), (1, 8, 2, 'Indian'), (1, 9, 2, 'Mediterranean'), (1, 10, 2, 'Argentinian'),
       (1, 11, 2, 'Peruvian'), (1, 12, 2, 'Arabic / Middle Eastern'), (1, 13, 2, 'American'), (1, 14, 2, 'Fusion'), (1, 15, 2, 'International'),
       (2, 1, 2, 'Vegetarian'), (2, 2, 2, 'Vegan'), (2, 3, 2, 'Gluten free / Celiac'), (2, 4, 2, 'Lactose free'),
       (2, 5, 2, 'Low calorie'), (2, 6, 2, 'Organic'), (2, 7, 2, 'Diabetic friendly (no added sugar)'),
       (3, 1, 2, 'Gourmet'), (3, 2, 2, 'Casual'), (3, 3, 2, 'Fast food'), (3, 4, 2, 'All-you-can-eat buffet'),
       (3, 5, 2, 'Bistro'), (3, 6, 2, 'Food truck'), (3, 7, 2, 'Traditional restaurant'), (3, 8, 2, 'Bar / Tapas'),
       (3, 9, 2, 'Coffee shop'), (3, 10, 2, 'Delivery'), (3, 11, 2, 'Fine dining'),
       (4, 1, 2, 'Budget / Low'), (4, 2, 2, 'Medium'), (4, 3, 2, 'High / Premium'), (4, 4, 2, 'Luxury');

-- Estados de las reservas (filtros del Req 16)
INSERT INTO dbo.estados_reservas (cod_estado, nom_estado)
VALUES (1, 'Pendiente'), (2, 'Cancelada'), (3, 'Sin evaluar'), (4, 'Evaluada');

INSERT INTO dbo.idiomas_estados (cod_estado, nro_idioma, estado)
VALUES (1, 1, 'Pendiente'), (1, 2, 'Pending'),
       (2, 1, 'Cancelada'), (2, 2, 'Cancelled'),
       (3, 1, 'Sin evaluar'), (3, 2, 'Not reviewed'),
       (4, 1, 'Evaluada'), (4, 2, 'Reviewed');
GO
