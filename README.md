# Архив миссий Токийского магического колледжа

Spring Web + DB приложение для лабораторной работы №3. Проект сделан на базе логики из предыдущей Java/Swing лабораторной: сохранены идеи единой модели миссии, парсеров разных форматов и генераторов отчетов, но теперь это веб-хаб с долговременным хранением в реляционной БД.

## Что реализовано

- загрузка миссий из JSON, YAML/YML, XML, TXT и event-log формата без расширения;
- нормализация данных в единую внутреннюю модель;
- сохранение миссий в реляционной БД через Spring Data JPA;
- повторная загрузка миссии с тем же `missionId` обновляет архивную запись;
- просмотр архива через веб-интерфейс и REST API;
- выбор миссии и генерация отчета из данных, заново извлеченных из БД;
- типы отчетов: подробный, краткий, по рискам;
- скачивание отчета как `.txt` файла;
- Swagger/OpenAPI: `/swagger-ui.html`;
- unit/integration tests для парсеров, сервиса и API;
- архитектурные артефакты: C4-схема и sequence diagram в папке `docs/`.

## Запуск с H2, без установки PostgreSQL

```bash
mvn spring-boot:run
```

После запуска:

- UI: `http://localhost:8080/`
- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/mission-archive`
  - User: `sa`
  - Password: пустой

## Запуск с PostgreSQL

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Параметры PostgreSQL лежат в `src/main/resources/application-postgres.yml`.

## Основные REST endpoints

| Метод | URL | Назначение |
|---|---|---|
| `POST` | `/api/missions/upload` | Загрузить файл миссии через multipart form-data |
| `POST` | `/api/missions` | Создать/обновить миссию JSON-запросом |
| `GET` | `/api/missions` | Получить архив миссий |
| `GET` | `/api/missions/{id}` | Получить полную миссию |
| `GET` | `/api/missions/{id}/report?type=DETAILED` | Получить отчет текстом |
| `GET` | `/api/missions/{id}/report/download?type=RISK` | Скачать отчет файлом |

## Пример JSON-запроса

```json
{
  "missionCode": "M-2024-017",
  "date": "2024-10-12",
  "location": "Токио, район Сибуя",
  "outcome": "SUCCESS",
  "damageCost": 1200000,
  "curse": {
    "name": "Проклятие подземного перехода",
    "threatLevel": "HIGH"
  },
  "sorcerers": [
    { "name": "Итадори Юдзи", "rank": "GRADE_1" }
  ],
  "techniques": [
    { "name": "Черная вспышка", "type": "INNATE", "owner": "Итадори Юдзи", "damage": 500000 }
  ]
}
```

## Структура проекта

```text
src/main/java/com/tokyo/magic/archive
├── config        # OpenAPI config
├── domain        # JPA entities and enums
├── dto           # API DTOs
├── parser        # parsers for JSON/YAML/XML/TXT/event-log
├── repository    # Spring Data repositories
├── service       # business logic, normalization, validation, reporting
└── web           # REST and MVC controllers
```

## Проверка

```bash
mvn test
```
