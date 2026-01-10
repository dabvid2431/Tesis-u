# StockApp — Proyecto de Tesis

Versión entregable del proyecto: backend Node.js + Android cliente para gestionar productos, compras, ventas y stock. Esta documentación explica cómo levantar el proyecto en un entorno limpio, ejecutar seeds/resets, usar Postman y preparar la entrega para la actividad de seguimiento de tesis.

## Contenido
- `app/` — Android app (Kotlin, MVVM, Retrofit).
- `stock_backend/` — Backend Node.js + Sequelize + PostgreSQL.
- `postman_collection_stockapp.json` — Colección Postman para probar la API.
- `postman_environment_stockapp.json` — Variables de entorno de Postman (base_url/emulator_base_url).

## Resumen funcional
La aplicación permite gestionar categorías, productos, proveedores, clientes, compras, ventas y movimientos de stock. Incluye CRUD para entidades principales y endpoints para consultar stock y reportes básicos.

## Arquitectura (breve)
- Backend: Express + Sequelize (Postgres). Endpoints REST en `stock_backend/src`.
- Android: Kotlin, arquitectura MVVM, repositorios que consumen la API via Retrofit (base URL configurable).
- Datos: PostgreSQL; `seed.js` y `reset-db.js` permiten inicializar y poblar datos de ejemplo.

## Tecnologías y versiones (recomendadas)
- Node.js >= 18
- PostgreSQL >= 12
- Java / Android Studio (versión reciente)
- Kotlin (Android)
- Dependencias backend: Express, Sequelize, pg, dotenv

## Requisitos previos
- Git
- Node.js y npm
- PostgreSQL instalado y accesible
- Android Studio (para compilar el cliente)
- Postman (opcional, para pruebas de la API)

## Variables de entorno
Coloca las variables en `stock_backend/.env` (no subir `.env` al repo). Usa `stock_backend/.env.example` como plantilla.

Variables mínimas (explicadas):

```
# Ejemplo en stock_backend/.env
PORT=3000
DATABASE_URL=postgres://usuario:password@localhost:5432/stockdb
NO_AUTH=true
```

- `PORT`: puerto donde corre el backend (3000 por defecto).
- `DATABASE_URL`: URL de conexión PostgreSQL.
- `NO_AUTH`: si `true`, el backend corre en modo local sin autenticación (documentado en la entrega).

## Pasos para levantar el backend (Windows PowerShell)

1) Abrir PowerShell y moverse al directorio del backend:

```powershell
cd 'c:\Users\PC\AndroidStudioProjects\stockapp\stock_backend'
```

2) Instalar dependencias:

```powershell
npm install
```

3) Crear la base de datos PostgreSQL (ejemplo):

```powershell
# en psql o PowerShell si tienes createdb en PATH
# usando psql interactivo:
psql -U postgres -c "CREATE DATABASE stockdb;"
# O usando createdb:
createdb -U postgres stockdb
```

4) Copiar `.env.example` a `.env` y editar con tus credenciales:

```powershell
copy .env.example .env
# luego editar .env con Notepad o VSCode
```

5) Ejecutar reset + seed para crear tablas y datos de ejemplo:

```powershell
npm run reset-db
```

6) Levantar el servidor en modo desarrollo:

```powershell
npm run dev
# o para producción:
npm start
```

El backend escuchará en `http://localhost:3000` por defecto. Usa `{{base_url}}` en Postman apuntando a `http://localhost:3000/api` (o la IP/host que uses).

## Ejecutar la app Android (breve)
- Abre el proyecto en Android Studio (`c:\Users\PC\AndroidStudioProjects\stockapp`).
- Asegúrate de que la variable `BuildConfig.BASE_URL` o la constante correspondiente en `RetrofitClient.kt` apunte a `http://10.0.2.2:3000/api` si usas emulador Android estándar.
- Ejecuta la app en un emulador o dispositivo físico.

Notas: si pruebas desde otro dispositivo en la misma red, ajusta `base_url` en `postman_environment_stockapp.json` o en `BuildConfig`.

## Postman
- Importa `postman_collection_stockapp.json` y selecciona el environment `postman_environment_stockapp.json`.
- Usa `Login` (si lo tienes) o ejecuta directamente los endpoints públicos.

## Seeds y reinicio de la base de datos
- `reset-db.js` eliminará y recreará tablas y ejecutará `seed()` para poblar datos por defecto.
- Comando: `npm run reset-db` (ejecutar desde `stock_backend/`).

## Ejecutar con Docker (recomendado para evaluadores)
Se incluye un `Dockerfile` y `docker-compose.yml` en la raíz para facilitar levantar un entorno reproducible (Postgres + backend).

Pasos rápidos desde la raíz del repo:

```powershell
# Construye y levanta servicios (db + backend)
docker-compose up --build

# Para ejecutar en background
docker-compose up --build -d

# Ver logs del backend
docker-compose logs -f backend

# Detener y eliminar contenedores
docker-compose down
```

Notas:
- El servicio de Postgres estará en el contenedor `db` y expone el puerto `5432` al host.
- El backend se expondrá en `http://localhost:3000`.
- Si necesitas ajustar credenciales del DB o variables de entorno, modifica `docker-compose.yml` o crea un `.env` para Docker Compose.

## Decisión de seguridad — Modo "no-auth"
Por elección de uso personal y para facilitar la evaluación, el proyecto incluye soporte para modo sin autenticación (`NO_AUTH=true`). Esto se debe documentar en la entrega. Si desplegas en red pública, cambia `NO_AUTH` a `false` y añade autenticación.

En el README del backend se indica claramente que la aplicación está en modo local sin auth para demo. Esto debe quedar registrado en Moodle.

## Invitar al docente como colaborador en GitHub
1. Ve al repositorio en GitHub.
2. Settings → Collaborators and teams → Add collaborator.
3. Introduce el identificador `JonathanDQS` o el nombre `Jonathan Quespaz` y envía la invitación.

## Checklist para la entrega en Moodle
- [ ] Repositorio privado en GitHub con todo el código.
- [ ] `README.md` en la raíz (este archivo).
- [ ] `.env.example` incluido en `stock_backend/`.
- [ ] `postman_collection_stockapp.json` y `postman_environment_stockapp.json` incluidos.
- [ ] `reset-db.js` y `seed.js` funcionando (ejecutar `npm run reset-db`).
- [ ] Confirmación de invitación al docente (añadir captura o nota en Moodle).
- [ ] (Opcional) PDF con capturas y notas.

### Enlace del repositorio y captura de invitación

Por favor agrega en este archivo o en la entrega de Moodle el enlace al repositorio privado de GitHub y una captura de pantalla que confirme que se invitó al docente `JonathanDQS` como colaborador (Settings → Collaborators and teams → Add collaborator). 

Ejemplo (pegado aquí):

```
Repositorio: https://github.com/dabvid2431/Tesis-u.git
Captura: docs/screenshots/invite_jonathandqs.png (sube la imagen al repo o adjúntala en Moodle)
```

## Archivos importantes
- `stock_backend/seed.js` — script para poblar datos.
- `stock_backend/reset-db.js` — recrea DB y ejecuta seed.
- `postman_collection_stockapp.json` — colección de pruebas.

## Puertos usados
- Backend API: `3000` (ruta: `http://localhost:3000/api`).
- Postgres (si no usas Docker): `5432`.

## Ejecutar tests

### Tests del Backend (Node.js)
Se incluyen tests básicos en `stock_backend/tests/` usando Jest + Supertest.

Recomendación: crea una base de datos separada para tests (por ejemplo `stockdb_test`) y ejecuta los tests apuntando a esa BD para no sobrescribir datos de desarrollo.

Ejemplo (PowerShell):

```powershell
# crear BD de pruebas
psql -U postgres -c "CREATE DATABASE stockdb_test;"

# ejecutar tests usando DATABASE_URL temporal
$env:DATABASE_URL='postgres://postgres:postgres@localhost:5432/stockdb_test'
npm test
```

Nota: los tests actuales llaman a `sequelize.sync({ force: true })` y eliminarán/crearán tablas en la BD indicada.

### Tests de Android (Kotlin)

#### Tests Unitarios
Los tests unitarios están en `app/src/test/java/com/tuempresa/stockapp/` y cubren los ViewModels principales:

- `ClientViewModelTest.kt` - Tests completos (fetch, create, createMap, update, delete)
- `SaleViewModelTest.kt` - Tests completos (fetch, create, createMap, update, delete)
- `ProductViewModelTest.kt` - Tests de productos
- `PurchaseViewModelTest.kt` - Tests de compras
- `CategoryViewModelTest.kt` - Tests de categorías
- `SupplierViewModelTest.kt` - Tests de proveedores
- `UserViewModelTest.kt` - Tests de usuarios

**Ejecutar tests unitarios:**

```powershell
# Desde la raíz del proyecto
./gradlew :app:testDebugUnitTest
```

#### Generar Reporte de Cobertura (JaCoCo)

```powershell
# Generar reporte de cobertura
./gradlew :app:jacocoTestReport

# Ver reporte HTML
# Abre: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

El reporte incluye:
- Cobertura de código por clase y método
- Reporte HTML interactivo
- Reporte XML para integración con Sonar

#### Tests Instrumentados (UI)
Los tests instrumentados están en `app/src/androidTest/` y requieren un dispositivo o emulador.

```powershell
./gradlew :app:connectedAndroidTest
```

## CI/CD

El proyecto incluye un pipeline de CI/CD configurado en `.github/workflows/ci.yml` que:

1. **Ejecuta tests del backend** (Node.js)
2. **Ejecuta tests unitarios de Android** y genera reporte JaCoCo
3. **Ejecuta análisis Sonar** (opcional, requiere secrets configurados)

### Configurar Sonar (Opcional)

Para habilitar el análisis de Sonar:

1. Crea un proyecto en [SonarCloud](https://sonarcloud.io) o tu instancia de SonarQube
2. Agrega los siguientes secrets en GitHub:
   - `SONAR_HOST_URL`: URL de tu instancia (ej: `https://sonarcloud.io`)
   - `SONAR_TOKEN`: Token de autenticación

El archivo `sonar-project.properties` está configurado para:
- Analizar código en `app/src/main/java`
- Incluir tests en `app/src/test/java`
- Importar reporte de cobertura JaCoCo
- Excluir clases generadas (R, BuildConfig, etc.)

## Calidad de Código

### Cobertura de Tests
- **Objetivo:** 70-80% de cobertura de código
- **Umbral en CI:** Se verifica automáticamente en el pipeline (actualmente 75%); el build fallará si la cobertura LINE es inferior.
- **Reporte:** Generado automáticamente con JaCoCo
- **Ubicación:** `app/build/reports/jacoco/jacocoTestReport/`
- **Cómo ajustar:** Modifica `--min` en `.github/scripts/check_jacoco_coverage.py` o actualiza el workflow step.

### Análisis Estático
- **Sonar:** Configurado para análisis continuo
- **Lint:** Ejecutar con `./gradlew :app:lint`

## Uso de Postman
- Importa `postman_collection_stockapp.json` y `postman_environment_stockapp.json`.
- Ajusta `{{base_url}}` si usas Docker (`http://localhost:3000/api`) o emulador (`http://10.0.2.2:3000/api`).
- Para crear compras de ejemplo usa el endpoint `POST /api/purchases` con un body similar al de la colección. Asegúrate de que `supplierId` y `productId` existan (usa `GET /api/products` y `GET /api/suppliers`).

## Troubleshooting (problemas comunes)
- Error de conexión a PostgreSQL: verifica que el servicio esté corriendo y que `DATABASE_URL` tenga credenciales correctas.
- `psql` o `createdb` no reconocidos: agrega la ruta de PostgreSQL al `PATH` o usa pgAdmin para crear la BD.
- Permisos/privilegios: asegúrate de que el usuario de la BD tenga permiso para crear tablas.
- Puerto 3000 en uso: cambia `PORT` en `.env` o mata el proceso que usa el puerto.
- Problemas con el emulador Android: usa `10.0.2.2` para apuntar al `localhost` del host.
- Tests fallan: verifica que las dependencias estén instaladas (`./gradlew build --refresh-dependencies`)

## Entrega en Moodle — archivos y comprobaciones que subir
- Enlace al repositorio privado en GitHub (pegar aquí).
- Confirmación de invitación al docente (`JonathanDQS`) — captura incluida en `docs/screenshots/` o adjunta en Moodle.
- `README.md` completo (este archivo).
- PDF opcional con capturas (recomendado para facilitar la evaluación).

## Buenas prácticas antes de entregar
- Ejecuta `npm run reset-db` y confirma que la app carga datos de ejemplo.
- Corre `npm test` apuntando a una BD de pruebas para confirmar que los tests pasan.
- Ejecuta `./gradlew :app:testDebugUnitTest :app:jacocoTestReport` y revisa la cobertura.
- Sube el `postman_collection_stockapp.json` y `postman_environment_stockapp.json` actualizados.
- Añade en el repositorio una carpeta `docs/screenshots/` con capturas de la app y la invitación enviada al docente.
- Verifica que el CI pasa correctamente en GitHub Actions.

---

**Fecha de última actualización:** 2025-01-XX
**Versión:** 1.0