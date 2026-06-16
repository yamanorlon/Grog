# 🛡️ Grog — Vulnerability Manager API

**Grog** — серверное REST API для управления жизненным циклом уязвимостей в рамках продуктов и проектов безопасности (pentest / security assessment). Система построена на Kotlin и Ktor, использует PostgreSQL, поддерживает автоматическую генерацию OpenAPI-документации и полноценный стек наблюдаемости (Prometheus, Grafana, ELK).

| Параметр | Значение              |
|----------|-----------------------|
| Группа артефактов | `com.yamanorlon.grog` |
| Версия | `0.0.1`               |
| JDK | 21                    |
| Порт API | `8080`                |

---

## 📋 Описание проекта

### Назначение системы

Grog — это **менеджер уязвимостей** (Vulnerability Manager), который централизует хранение и управление данными о:

- **Products** — программных системах, подлежащих оценке безопасности;
- **Engagements** — проектах/сессиях тестирования, привязанных к продукту;
- **Findings** — конкретных уязвимостях, обнаруженных в рамках оценки.

Система предоставляет единый HTTP API для CRUD-операций над этими сущностями, валидации входных данных, пагинации, фильтрации и аудита изменений.

### Какие задачи решает

- Структурированное хранение результатов security-оценок в реляционной БД.
- Связывание находок с продуктами и проектами через внешние ключи (`product → engagement → finding`).
- Единообразный REST-интерфейс для интеграции с внешними сканерами, CI/CD и внутренними инструментами.
- Контроль целостности данных: валидация на уровне API, optimistic locking, ограничения БД.
- Наблюдаемость: структурированные логи, метрики HTTP-запросов, дашборды Grafana.

### Бизнес-ценность

- Сокращение времени на учёт и triage уязвимостей за счёт единого реестра.
- Прозрачная иерархия «продукт → оценка → сработка» для отчётности и приоритизации.
- Готовность к эксплуатации: Docker Compose, health-check, мониторинг, автотесты.
- Машиночитаемая документация API (OpenAPI / Swagger UI) для команд разработки и QA.

### Предпосылки создания

- Рост числа инструментов сканирования и необходимость единой точки агрегации находок.
- Потребность в API-first подходе для автоматизации security-процессов.
- Требования учебного / исследовательского проекта: демонстрация многослойной архитектуры, тестирования и DevOps-практик.

### Целевая аудитория

- Инженеры по безопасности (AppSec, DevSecOps, Pentest Engeeners).
- Backend-разработчики, интегрирующие сканеры и пайплайны.
- QA-инженеры, проводящие функциональное и нагрузочное тестирование API.

### Ожидаемый результат использования

После развёртывания пользователь получает работающий REST API с документацией, мониторингом и возможностью:

1. Создавать продукты и привязывать к ним оценки безопасности.
2. Регистрировать находки с severity, CVSS, CVE/CWE, статусом и рекомендациями.
3. Фильтровать и пагинировать данные через query-параметры.
4. Отслеживать состояние сервиса через `/health`, `/metrics`, Grafana и Kibana.

---

## ⚙️ Функциональные возможности

### Управление продуктами (Products)

| Операция | Метод | Путь |
|----------|-------|------|
| Создание | `POST` | `/api/products` |
| Список | `GET` | `/api/products` |
| Получение по ID | `GET` | `/api/products/{id}` |
| Обновление | `PUT` | `/api/products/{id}` |
| Удаление | `DELETE` | `/api/products/{id}` |

**Дополнительно:**

- Фильтрация: `name`, `owner`, `tag`
- Пагинация: `page`, `size`
- Сортировка: `sort` (например `name`, `-createdAt`)
- Валидация: обязательное имя (не пустое после trim), ограничения длины полей (Konform)
- Optimistic locking через поле `version` при обновлении
- Аудит: заголовок `X-User-Id` записывается в `createdBy` / `updatedBy`

### Управление оценками (Engagements)

| Операция | Метод | Путь |
|----------|-------|------|
| Создание | `POST` | `/api/engagements` |
| Список | `GET` | `/api/engagements` |
| Получение по ID | `GET` | `/api/engagements/{id}` |
| Обновление | `PUT` | `/api/engagements/{id}` |
| Удаление | `DELETE` | `/api/engagements/{id}` |

**Дополнительно:**

- Привязка к существующему `productId` (проверка ссылочной целостности)
- Фильтрация: `productId`, `status`, `name`
- Статусы: `Planned`, `InProgress`, `Completed`, `Cancelled`
- Валидация дат: `startDate` / `endDate` в формате ISO-8601, `endDate >= startDate`
- Ограничение БД: `CHECK (end_date IS NULL OR end_date >= start_date)`

### Управление cработками (Findings)

| Операция | Метод | Путь |
|----------|-------|------|
| Создание | `POST` | `/api/findings` |
| Список | `GET` | `/api/findings` |
| Получение по ID | `GET` | `/api/findings/{id}` |
| Обновление | `PUT` | `/api/findings/{id}` |
| Удаление | `DELETE` | `/api/findings/{id}` |

**Дополнительно:**

- Привязка к `engagementId`
- Поля: `title`, `description`, `severity`, `status`, `cvssScore`, `cve`, `cwe`, `mitigation`, `impact`, `references`, `discoveredDate`
- Severity: `Critical`, `High`, `Medium`, `Low`, `Info`
- Фильтрация: `engagementId`, `severity`, `status`, `title`
- CVSS: диапазон 0.0–10.0

### OpenAPI / Swagger

- Автоматическая генерация спецификации при старте приложения (Ktor OpenAPI plugin).
- Статическая HTML-документация в каталоге `docs/` (настраивается через `OPENAPI_OUTPUT_DIR`).
- **Swagger UI:** [http://localhost:8080/swagger](http://localhost:8080/swagger)
- **OpenAPI JSON:** [http://localhost:8080/openapi](http://localhost:8080/openapi)
- Аннотации `describe { }` на маршрутах описывают операции, параметры и коды ответов.

### Мониторинг

| Компонент | Назначение |
|-----------|------------|
| **Prometheus** | Сбор метрик с `/metrics` API (scrape каждые 15 с) |
| **Grafana** | Визуализация HTTP-метрик, JVM, латентности; дашборд `vulnerability-manager-api` |
| **Elasticsearch** | Хранение структурированных логов |
| **Logstash** | Приём логов по TCP (порт 5044) от Logback appender |
| **Kibana** | Поиск и анализ логов приложения |

**Логи приложения:**

- Logback + `logstash-logback-encoder` — JSON в stdout и Logstash.
- MDC-поля: `traceId`, `method`, `path`, `status`, `durationMs`.
- Заголовок `X-Trace-Id` для сквозной трассировки запросов.

**Системные метрики (Micrometer):**

- HTTP: `ktor_http_server_requests_seconds` (histogram, percentiles).
- JVM: память, GC.
- CPU: `ProcessorMetrics`.
- Алерты Prometheus: высокий процент 5xx, p95 latency > 1 с.

---

## 🧪 Тестирование

Проект покрыт многоуровневым набором автотестов (~68 тест-кейсов).

### Unit Tests

Расположение: `core/api/src/test/kotlin/.../unit/service/`

| Класс | Покрытие |
|-------|----------|
| `ProductServiceTest` | create, get, list, update, delete, валидация, conflict |
| `EngagementServiceTest` | CRUD, проверка productId, валидация дат |
| `FindingServiceTest` | CRUD, проверка engagementId, валидация полей |

- Фреймворк: **JUnit 5** + **MockK**
- Репозитории мокируются; БД не используется.

### Integration Tests (Repository)

Расположение: `core/data/src/test/kotlin/.../integration/repository/`

| Класс | Покрытие |
|-------|----------|
| `ProductRepositoryIntegrationTest` | insert, select, update, delete, optimistic locking, фильтры |
| `EngagementRepositoryIntegrationTest` | CRUD, FK-нарушения, existsProduct |
| `FindingRepositoryIntegrationTest` | CRUD, FK-нарушения, фильтр по severity |

### API Integration Tests (Ktor)

Расположение: `core/api/src/test/kotlin/.../api/`

| Класс | Покрытие |
|-------|----------|
| `ProductApiIntegrationTest` | полный CRUD lifecycle, 400 на пустое имя |
| `EngagementApiIntegrationTest` | CRUD, 400 при несуществующем product |
| `FindingApiIntegrationTest` | CRUD, 400 при пустом title |
| `ApiNegativeIntegrationTest` | malformed JSON, пустой payload, невалидные UUID, 404 |

- **Ktor `testApplication`** + HTTP-клиент с JSON-сериализацией.
- Базовый класс: `ApiIntegrationTest` → `AbstractIntegrationTest`.

### Testcontainers

Общая инфраструктура в `core/data/src/testFixtures/`:

| Компонент | Роль |
|-----------|------|
| `PostgresTestContainer` | Singleton PostgreSQL 17 (Testcontainers) |
| `TestDatabaseBootstrap` | Flyway migrate → `Database.connect()` → `SchemaUtils.createMissingTablesAndColumns` |
| `DatabaseCleaner` | `TRUNCATE` таблиц между тестами |
| `AbstractIntegrationTest` | `@BeforeAll` bootstrap, `@BeforeEach` cleanup |

Порядок инициализации гарантирует отсутствие ошибок `relation does not exist` и `Please call Database.connect()`.

### Test Factories

- `RequestTestFactory` — DTO запросов API (валидные и невалидные данные).
- `EntityTestFactory` — доменные сущности для repository-тестов.

### Команды запуска тестов

```bash
# Все модули
./gradlew test

# Только API (unit + integration)
./gradlew :core:api:test

# Только repository integration
./gradlew :core:data:test

# Конкретный класс
./gradlew :core:api:test --tests "com.yamanorlon.grog.api.ProductApiIntegrationTest"
```

**Требования:** Docker (Testcontainers `disabledWithoutDocker = true` — без Docker integration-тесты пропускаются).

### Smoke-тесты (bash)

```bash
docker compose up -d
```

---

## 🏗️ Архитектура проекта

Монорепозиторий Gradle с модулями:

```
grog/
├── core/
│   ├── api/          # Транспортный слой (Ktor, маршруты, сервисы, конфигурация)
│   ├── domain/       # Доменные контракты (интерфейсы репозиториев, исключения)
│   ├── data/         # Слой данных (Exposed, реализации репозиториев)
│   └── model/        # Доменные модели и enum-типы
├── observability/    # Конфигурация Prometheus, Grafana, Logstash, Kibana
├── docker-compose.yml
└── Dockerfile
```

### Транспортный модуль (`core:api`)

Отвечает за HTTP API и сквозную инфраструктуру:

- **Маршруты:** `api/route/ProductRoutes.kt`, `EngagementRoutes.kt`, `FindingRoutes.kt`
- **DTO:** `api/request/`, `api/response/`, `api/mapper/`
- **Сервисы:** `service/ProductService.kt`, `EngagementService.kt`, `FindingService.kt`
- **Валидация:** `validation/RequestValidators.kt` (Konform)
- **Обработка ошибок:** `error/ExceptionHandler.kt` (StatusPages)
- **Конфигурация:** `config/` (Koin, DatabaseFactory, AppConfig)
- **Безопасность:** `security/SecurityHeaders.kt`
- **Наблюдаемость:** `observability/` (Micrometer, request tracing)
- **Точка входа:** `Application.kt`

### Логический модуль (`core:domain` + `core:api/service`)

- Интерфейсы репозиториев: `ProductRepository`, `EngagementRepository`, `FindingRepository`
- Доменные исключения: `NotFoundException`, `ValidationException`, `ConflictException`, `ReferenceNotFoundException`
- Бизнес-правила в сервисах: валидация, проверка ссылок, optimistic locking, аудит

### Модуль хранения данных (`core:data`)

- **Таблицы Exposed:** `database/table/Tables.kt` (`ProductsTable`, `EngagementsTable`, `FindingsTable`)
- **Репозитории:** `database/repository/*RepositoryImpl.kt`
- **Миграции:** `core/api/src/main/resources/db/migration/V1__init_schema.sql` (Flyway)
- **Инициализация:** `config/DatabaseFactory.kt` — Flyway + `Database.connect()`

### Модуль документации

- Ktor OpenAPI + Swagger UI plugins
- Генерация `docs/index.html` и JSON-спецификации при старте
- Маршруты `/openapi`, `/swagger`

### Модуль мониторинга

- **В приложении:** Micrometer Prometheus registry, endpoint `/metrics`, structured logging
- **Инфраструктура:** `observability/prometheus/`, `observability/grafana/`, `observability/logstash/`, Kibana в `docker-compose.yml`

### Модуль тестирования

- Unit: `core/api/src/test/.../unit/`
- API integration: `core/api/src/test/.../api/`
- Repository integration: `core/data/src/test/.../integration/`
- Shared fixtures: `core/data/src/testFixtures/.../testsupport/`

---

## 🛠️ Используемые технологии

| Категория | Технология | Версия |
|-----------|------------|--------|
| Язык | Kotlin | 2.2.0 |
| Runtime | Java (Toolchain) | 21 |
| HTTP Framework | Ktor Server | 3.4.3 |
| DI | Koin | 4.0.4 |
| ORM | Exposed | 0.53.0 |
| СУБД | PostgreSQL | 17 |
| Миграции | Flyway | 11.7.2 |
| Валидация | Konform | 0.11.0 |
| Сериализация | kotlinx.serialization | (Kotlin plugin) |
| Логирование | Logback  | 1.5.18 / 8.1 |
| Метрики | Micrometer + Prometheus Registry | 1.14.6 |
| Мониторинг | Prometheus | 2.55.1 |
| Визуализация | Grafana | 11.3.1 |
| Логи | ELK Stack (Elasticsearch, Logstash, Kibana) | 8.15.3 |
| Unit-тесты | JUnit 5 | 5.12.2 |
| Моки | MockK | 1.14.2 |
| Integration Testing | Testcontainers (PostgreSQL) | 1.21.0 |
| Контейнеризация | Docker + Docker Compose | — |
| Сборка | Gradle | 9.4.1 |

---

## 📁 Структура проекта

```
grog/
├── build.gradle.kts                 # Корневая конфигурация Gradle
├── settings.gradle.kts              # Модули монорепозитория
├── docker-compose.yml               # PostgreSQL, API, ELK, Prometheus, Grafana
├── Dockerfile                       # Multi-stage сборка API-образа
├── Makefile                         # smoke-test, performance-test, test
│
├── core/
│   ├── api/
│   │   └── src/
│   │       ├── main/kotlin/com/yamanorlon/grog/
│   │       │   ├── Application.kt
│   │       │   ├── api/
│   │       │   │   ├── mapper/          # Entity → Response
│   │       │   │   ├── request/         # Create/Update DTO
│   │       │   │   ├── response/        # API responses, ErrorResponse
│   │       │   │   └── route/           # Product, Engagement, Finding routes
│   │       │   ├── config/              # Koin, DatabaseFactory, AppConfig
│   │       │   ├── error/               # Global exception handling
│   │       │   ├── observability/       # Metrics, tracing
│   │       │   ├── security/            # HTTP security headers
│   │       │   ├── service/             # Business services
│   │       │   ├── util/                # QueryParams, DateTimeUtils
│   │       │   └── validation/          # Konform validators
│   │       ├── main/resources/
│   │       │   ├── application.yaml
│   │       │   ├── logback.xml
│   │       │   └── db/migration/        # Flyway SQL
│   │       └── test/kotlin/.../
│   │           ├── api/                 # Ktor HTTP integration tests
│   │           ├── unit/service/        # Unit tests (MockK)
│   │           ├── factories/           # Test data builders
│   │           └── testcontainers/      # ApiIntegrationTest base
│   │
│   ├── domain/
│   │   └── src/main/kotlin/.../
│   │       ├── domain/repository/       # Repository interfaces
│   │       └── domain/exception/        # Domain exceptions
│   │
│   ├── data/
│   │   └── src/
│   │       ├── main/kotlin/.../database/
│   │       │   ├── table/Tables.kt
│   │       │   └── repository/        # Exposed implementations
│   │       ├── test/kotlin/.../integration/repository/
│   │       └── testFixtures/kotlin/.../testsupport/
│   │           ├── AbstractIntegrationTest.kt
│   │           ├── PostgresTestContainer.kt
│   │           ├── TestDatabaseBootstrap.kt
│   │           └── DatabaseCleaner.kt
│   │
│   └── model/
│       └── src/main/kotlin/.../domain/model/
│           ├── Product.kt, Engagement.kt, Finding.kt
│           └── Severity, EngagementStatus, FindingStatus, Page...
│
├── observability/
│   ├── prometheus/                    # prometheus.yml, alerts.yml
│   ├── grafana/                       # datasources, dashboards
│   ├── logstash/                      # pipeline, config
│   └── kibana/                        # saved_objects.ndjson
│
├── scripts/
│   ├── api-smoke-test.sh
│   ├── generate-test-data.sh
│   ├── run-performance-tests.sh
│   └── generate-performance-report.sh
│
└── performance/
    ├── load-test.js                   # k6 сценарии
    └── README.md
```

---

## 🚀 Запуск проекта

### Требования

| Компонент | Версия / примечание |
|-----------|---------------------|
| **Java** | JDK 21 |
| **Gradle** | 9.4.1 (wrapper в репозитории) |
| **Docker** | Для Compose и Testcontainers |
| **Docker Compose** | v2+ |

### Запуск через Docker Compose

Полный стек: PostgreSQL, API, Elasticsearch, Logstash, Kibana, Prometheus, Grafana.

```bash
# Сборка и запуск всех сервисов
docker compose up -d

# Проверка состояния
docker compose ps

# Проверка API
curl http://localhost:8080/health

# Остановка
docker compose down
```

Сервис `api` ожидает готовности PostgreSQL и Logstash, выполняет Flyway-миграции при старте и публикует порт `8080`.

### Локальный запуск (без Docker для API)

```bash
# 1. Запустить только PostgreSQL
docker compose up -d postgres

# 2. Запустить API через Gradle
./gradlew :core:api:run

# Альтернатива: собрать дистрибутив
./gradlew :core:api:installDist
./core/api/build/install/api/bin/api
```

Переменные окружения (имеют значения по умолчанию в `application.yaml`):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/vulnmanager
export DB_USER=postgres
export DB_PASSWORD=postgres
export METRICS_ENABLED=true
export LOGSTASH_ENABLED=false
```

---

## 🌐 Работа с API

### Основные endpoints

| Ресурс | Базовый путь |
|--------|--------------|
| Products | `/api/products` |
| Engagements | `/api/engagements` |
| Findings | `/api/findings` |

### Служебные endpoints

| Назначение | URL |
|------------|-----|
| Health Check | [http://localhost:8080/health](http://localhost:8080/health) |
| Prometheus metrics | [http://localhost:8080/metrics](http://localhost:8080/metrics) |
| OpenAPI JSON | [http://localhost:8080/openapi](http://localhost:8080/openapi) |
| Swagger UI | [http://localhost:8080/swagger](http://localhost:8080/swagger) |
| Debug log test | [http://localhost:8080/debug/log-test](http://localhost:8080/debug/log-test) |

### Пример: создание Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-User-Id: security-analyst" \
  -d '{
    "name": "Payments API",
    "description": "Core payment processing service",
    "owner": "platform-team",
    "tags": ["pci", "critical"]
  }'
```

### Пример: создание Engagement

```bash
curl -X POST http://localhost:8080/api/engagements \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "<PRODUCT_UUID>",
    "name": "Q1 2026 Pentest",
    "description": "Annual security assessment",
    "target": "https://api.example.com",
    "status": "Planned",
    "startDate": "2026-01-15T10:00:00Z"
  }'
```

### Коды ответов

| Код | Ситуация |
|-----|----------|
| `201` | Успешное создание |
| `200` | Успешное чтение / обновление |
| `204` | Успешное удаление |
| `400` | Ошибка валидации, невалидный JSON, неверная ссылка |
| `404` | Сущность не найдена |
| `409` | Конфликт optimistic locking |
| `500` | Необработанная ошибка |

Формат ошибки — `ErrorResponse` с полями `timestamp`, `status`, `error`, `message`, `path`, `details[]`.

---

## 📊 Мониторинг

После `docker compose up -d`:

| Сервис | URL | Учётные данные |
|--------|-----|----------------|
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin` / `admin` |
| **Prometheus** | [http://localhost:9090](http://localhost:9090) | — |
| **Kibana** | [http://localhost:5601](http://localhost:5601) | без аутентификации |
| **Elasticsearch** | [http://localhost:9200](http://localhost:9200) | — |

**Grafana** автоматически подключает:

- Prometheus (`http://prometheus:9090`) — HTTP/JVM метрики API.
- Elasticsearch — индекс логов `vulnerability-manager-logs-*`.

**Prometheus alerts** (`observability/prometheus/alerts.yml`):

- `HighErrorRate` — доля 5xx > 5% за 5 минут.
- `HighLatencyP95` — p95 латентности > 1 с.

---

### Собираемые метрики

- `http_req_duration` — время ответа HTTP.
- `errors` — доля неуспешных запросов.
- `product_list_duration`, `engagement_list_duration`, `finding_list_duration` — латентность списков.
- `write_duration` — латентность операций записи.
- На стороне Prometheus/Grafana: RPS, p50/p95/p99, JVM memory, GC, CPU.

---

## 🔒 Безопасность

| Мера | Реализация |
|------|------------|
| **Валидация входных данных** | Konform-валидаторы для всех Create/Update DTO; trim строк; проверка UUID, дат, CVSS |
| **Обработка исключений** | Централизованный `StatusPages` — без утечки stack trace клиенту |
| **Security headers** | `X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`, `Referrer-Policy`, `Permissions-Policy` |
| **Целостность БД** | FK `ON DELETE CASCADE`, CHECK-ограничения, уникальные индексы |
| **Optimistic locking** | Поле `version` предотвращает lost updates |
| **Аудит** | `createdBy` / `updatedBy` через заголовок `X-User-Id` |
| **Трассировка** | `X-Trace-Id` в ответах и MDC логов |
| **Ограничение тела запроса** | `app.request.maxSizeBytes` (по умолчанию 1 МБ) |

> **Примечание:** модуль `plugins:auth` зарезервирован для будущей аутентификации. В текущей версии API не требует токенов — рекомендуется развёртывание за reverse proxy с auth в production.

---

## 🗄️ Схема базы данных

Миграция `V1__init_schema.sql`:

```
products (id, name, description, owner, tags[], created_at, updated_at, created_by, updated_by, version)
    │
    └── engagements (id, product_id FK, name, status, start_date, end_date, ..., version)
            │
            └── findings (id, engagement_id FK, title, severity, status, cvss_score, cve, cwe, ..., version)
```

- Расширение `pgcrypto` для `gen_random_uuid()`.
- GIN-индекс на `products.tags`.
- Индексы на поля фильтрации: `name`, `owner`, `status`, `severity`, `engagement_id`.

---

## ✅ Заключение

**Grog** — полнофункциональный Vulnerability Manager API, демонстрирующий:

- многослойную архитектуру на Kotlin (api → domain → data → model);
- production-практики: Flyway, Docker, health-checks, structured logging, Prometheus/Grafana/ELK;
- комплексное тестирование: unit (MockK), repository integration (Testcontainers + Exposed), API integration (Ktor test host);
- автоматическую документацию OpenAPI/Swagger;
- инструменты нагрузочного и smoke-тестирования.