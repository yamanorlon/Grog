rootProject.name = "grog"

include("core:api")                     // Ktor сервер, маршруты, контроллеры
include("core:domain")                  // Бизнес-логика, интерфейсы репозиториев
include("core:data")                    // Реализация репозиториев (Exposed/JDBC, MongoDB и т.д.)
include("core:model")                   // DTO, Entity, общие модели (сериализация)
include("plugins:auth")                 // Аутентификация/Авторизация
include("plugins:scanner-adapters")     // Адаптеры для импорта сканов (Trivy, SonarQube, etc.)