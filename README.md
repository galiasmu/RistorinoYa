```markdown
# Ristorino — Guía técnica del proyecto (Backend + Frontend)

> **Objetivo:** Documentar cómo está armado el sistema Ristorino (Spring Boot + Angular + Mock Node), qué hace cada archivo/carpeta y cómo correr una demo end-to-end.

---

## 1) Visión general

**Ristorino** es un portal que publica promociones enviadas por restaurantes externos.  
Registra clics de usuarios sobre esas promociones y notifica asincrónicamente a los restaurantes.

**Componentes:**

* 🟢 **Backend (Spring Boot)** — expone APIs públicas del portal, guarda promociones/restaurantes y registra clics. Además ejecuta un *scheduler* para notificar clics.
* 🟠 **Restaurante Mock (Node/Express)** — simula un restaurante: envía promociones al portal y recibe notificaciones de clics.
* 🔵 **Frontend (Angular)** — lista promociones, registra clics y muestra detalle del restaurante.

---

## 2) Arquitectura funcional (flujo)

```

Restaurante Mock (Node, :9090)
└─(POST /demo/publish-promo)→ Ristorino (Spring, :8081)
├─ guarda Restaurant + Promotion
Frontend (Angular, :4200) ──GET /api/promotions──▶
Frontend (click) ──POST /api/clicks──▶ Ristorino
Ristorino (scheduler) ──POST /demo/notify-click──▶ Restaurante Mock

````

**Paso a paso:**

1. El Mock envía una promoción a `/api/external/promotions` (vía `POST /demo/publish-promo`).
2. Backend crea/actualiza **Restaurant** y guarda **Promotion**.
3. Front consulta `GET /api/promotions` y renderiza tarjetas.
4. Usuario hace clic → front envía `POST /api/clicks {promotionId}`.
5. Backend marca el click como `PENDING`.
6. Un **scheduler** recoge pendientes y notifica a `POST /demo/notify-click` del restaurante.
7. Mock responde **OK** y el backend marca el click como `SENT`.

---

## 3) Backend (Spring Boot)

**Puertos y propiedades (ejemplo):**

```properties
server.port=8081
spring.application.name=RistorinoYa

ristorino.notification.rest.base-url=http://localhost:9090
ristorino.notification.rest.path=/demo/notify-click
ristorino.notification.rest.enabled=true
ristorino.notification.retries.max-attempts=5
ristorino.notification.retries.fixed-delay-ms=3000

spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
````

**Paquetes/Clases (resumen):**

```
com.portal.ristorinoya
├── controller
│   ├── PromotionController         // GET /api/promotions
│   ├── ClickController             // POST /api/clicks
│   ├── RestaurantController        // GET /api/restaurants/{id}
│   └── ExternalIngestController    // POST /api/external/promotions
├── dto
│   ├── PromotionDTO, RestaurantDTO, ClickCreateDTO, ExternalPromotionInDTO
├── entity
│   ├── Promotion, Restaurant, ClickEvent
├── repository
│   ├── PromotionRepository, RestaurantRepository, ClickEventRepository
├── scheduler
│   └── ClickNotifyScheduler        // notifica clics a restaurantes
└── client.restaurantnotify
    ├── RestaurantNotifyClient, RestRestaurantNotifyClient
```

**Contratos API (usados por el front):**

* `GET /api/promotions` → `{ content: Promotion[] }`
* `POST /api/clicks`    → `{ clickId: number }`
* `GET /api/restaurants/{id}` → `Restaurant`

**CORS (desarrollo):**

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
  return new WebMvcConfigurer() {
    @Override public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
        .allowedOrigins("http://localhost:4200")
        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
  };
}
```

---

## 4) Mock Restaurante (Node/Express)

**package.json** (mínimo):

```json
{
  "name": "ristorino-mock-restaurant",
  "version": "1.0.0",
  "type": "module",
  "main": "server.js",
  "scripts": { "start": "node server.js" },
  "dependencies": { "express": "^4.19.2" }
}
```

**server.js** (endpoints):

```js
import express from 'express';
const app = express();
app.use(express.json());

// recibe notificaciones de clics
app.post('/demo/notify-click', (req, res) => {
  console.log('[MOCK] Petición recibida:', req.body);
  res.json({ ok: true });
});

// publica una promo hacia Ristorino
app.post('/demo/publish-promo', async (req, res) => {
  const target = 'http://localhost:8081/api/external/promotions';
  const resp = await fetch(target, {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req.body)
  });
  const data = await resp.json();
  res.status(resp.status).json({ ok: resp.ok, created: data });
});

app.listen(9090, () => console.log('[MOCK] Escuchando en http://localhost:9090'));
```

---

## 5) Frontend (Angular)

**Stack:** Angular 20 (standalone app shell) + feature module `HomeModule`.

**Estructura relevante:**

```
src/
├── app/
│   ├── app.html                       // <router-outlet />
│   ├── app.config.ts                  // provideRouter + provideHttpClient
│   ├── app.routes.ts                  // carga lazy de HomeModule
│   └── home/
│       ├── home.module.ts             // declara componentes + importa Common/HTTP/Router
│       ├── home-routing.module.ts     // rutas feature ('', 'restaurantes/:id')
│       ├── components/
│       │   ├── home/...
│       │   └── promotion-card/...
│       ├── pages/
│       │   └── restaurant-detail/...
│       ├── models/
│       │   ├── promotion.model.ts
│       │   └── restaurant.model.ts
│       └── services/
│           ├── promotions.service.ts
│           └── click.service.ts
└── environments/
    ├── environment.ts
    └── environment.development.ts
```

**Endpoints que consume el front:**

* `GET /api/promotions`
* `POST /api/clicks`
* `GET /api/restaurants/{id}`

---

## 6) Cómo correr una demo end-to-end

1. **Backend** (Spring Boot) en `:8081`

   ```bash
   mvn spring-boot:run
   ```
2. **Mock** (Node) en `:9090`

   ```bash
   npm install
   npm start
   ```
3. **Publicar una promoción**

   ```bash
   curl -X POST http://localhost:9090/demo/publish-promo \
     -H "Content-Type: application/json" \
     -d '{
       "restaurantName": "Trattoria Roma",
       "title": "2x1 en pastas (mock)",
       "description": "Promo enviada por el mock al portal",
       "imageUrl": "https://picsum.photos/seed/roma/800/450",
       "startAt": "2025-10-30T00:00:00Z",
       "endAt": "2025-11-10T00:00:00Z",
       "active": true,
       "priority": 7
     }'
   ```
4. **Frontend (Angular)** en `:4200`

   ```bash
   npm install
   ng serve -o
   ```
5. Verás las promociones en Home (`GET /api/promotions`).
   Al hacer clic:

    * Registra `POST /api/clicks`
    * Abre detalle `GET /api/restaurants/:id`
6. La consola del mock mostrará notificaciones del scheduler:

   ```
   [MOCK] Petición recibida:
   { restaurantId: 1, promotionId: 1, clickedAt: '...', source: 'Ristorino' }
   ```

---

## 7) Troubleshooting rápido

* **CORS error** → habilitar origen `http://localhost:4200`.
* **Sin tarjetas** → revisar `GET /api/promotions`.
* **500 en `/api/clicks`** → promotionId inválido.
* **404 `/api/restaurants/:id`** → no existe el restaurante.
* **Scheduler no notifica** → mock no está corriendo o puerto incorrecto.

---

## 8) Roadmap sugerido

* **UI/UX:** usar Angular Material o Tailwind para mejorar diseño.
* **Auth (futuro):** JWT + roles (Admin/Client).
* **Docker Compose:** para levantar Spring + Node juntos.
* **Swagger UI:** exponer `/swagger-ui/index.html` para documentación.
* **Datos iniciales:** agregar `schema.sql` + `data.sql` con un restaurante demo.

---

## 9) Glosario mínimo

* **Promotion:** oferta publicada por el restaurante.
* **ClickEvent:** registro de interacción del usuario con una promoción.
* **Scheduler:** tarea en segundo plano que notifica clics.
* **Mock:** servicio Node que emula al restaurante.

---