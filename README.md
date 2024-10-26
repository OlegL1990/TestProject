
# Тестовое микросервисное приложение.

Микросервисное приложение, созданное с использованием Spring Boot, Spring Security, Feign Client и PostgreSQL. Приложение состоит из трёх микросервисов:

1. **Сервис Пользователи**: Управляет информацией о пользователях.
2. **Сервис Роли**: Управляет информацией о ролях.
3. **Сервис Роли Пользователей**: Управляет назначением ролей пользователям.

Каждый микросервис взаимодействует с другими с помощью Feign-клиентов и защищённой аутентификации через Spring Security.

---

## Содержание

- [Начало работы](#начало-работы)
- [Предварительные требования](#предварительные-требования)
- [Установка](#установка)
- [Запуск приложения](#запуск-приложения)
- [Документация API](#документация-api)
- [Тестирование](#тестирование)

---

## Начало работы

### Предварительные требования

Убедитесь, что у вас установлены следующие компоненты:

- **Java 17** или выше
- **Maven 3.8** или выше
- **Docker** (для запуска баз данных PostgreSQL в контейнерах)

### Клонирование репозитория

Клонируйте репозиторий на локальный компьютер:

```bash
git clone https://github.com/your-username/your-repository-name.git
cd your-repository-name
```

## Установка

1. **Настройка переменных окружения**:

   Каждый микросервис использует свои переменные для доступа к базе данных и настройке порта.

2. **Настройка Docker для PostgreSQL**:

   Запустите три контейнера PostgreSQL для трёх разных баз данных, используя команды ниже:

   ```bash
   docker run --name postgres-userdb -e POSTGRES_DB=UserDB -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
   docker run --name postgres-roledb -e POSTGRES_DB=RoleDB -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5433:5432 -d postgres
   docker run --name postgres-userroledb -e POSTGRES_DB=UserRoleDB -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5434:5432 -d postgres
   ```

3. **Конфигурация базы данных для микросервисов**:

   В файле `application.properties` каждого микросервиса в папке `src/main/resources/` для подключения к своей базе данных:

   - **Сервис Пользователи**:

     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/UserDB
     spring.datasource.username=postgres
     spring.datasource.password=password
     ```

   - **Сервис Роли**:

     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5433/RoleDB
     spring.datasource.username=postgres
     spring.datasource.password=password
     ```

   - **Сервис Роли Пользователей**:

     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5434/UserRoleDB
     spring.datasource.username=postgres
     spring.datasource.password=password
     ``

---

## Запуск приложения

### Запуск с использованием Maven

Каждый микросервис можно запускать независимо. Перейдите в директорию нужного микросервиса (`UserService`, `RoleService`, `UserRoleService`) и выполните:

```bash
mvn spring-boot:run
```

### Доступ к Swagger-документации

После запуска приложения, доступ к Swagger для каждого микросервиса можно получить по следующим URL:

- `http://localhost:8080/swagger-ui.html` для **Сервиса Пользователи**
- `http://localhost:8081/swagger-ui.html` для **Сервиса Роли**
- `http://localhost:8082/swagger-ui.html` для **Сервиса Роли Пользователей**

---

## Документация API

Документация API доступна через Swagger по указанным выше ссылкам для каждого сервиса.

## Тестирование

### Запуск Unit и интеграционных тестов

Unit и интеграционные тесты доступны в каждом микросервисе. Чтобы запустить тесты, перейдите в корневую директорию сервиса и выполните:

```bash
mvn test
```

### Конфигурация тестовой базы данных

В файле `application-test.properties` каждого микросервиса сконфигурирована тестовая база данных:

Пример конфигурации для H2:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

### Установка активного профиля для тестов

Необходимые тесты сконфигурированы на использование профиля `test`.


---


