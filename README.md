# Professional Registry API

REST API built with Java 17 and Spring Boot to manage professionals and their contact information.

## Requirements
- Java 17+
- Maven 3+
- PostgreSQL (default configuration expects database `cadpro` on `localhost:5432` with user/password `postgres`)

## Configuration
Default database and JPA properties live in `src/main/resources/application.properties`. Adjust them to match your local environment if needed.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cadpro
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

## Running the application
1. Install dependencies and start the application:
   ```bash
   ./mvnw spring-boot:run
   ```
2. The API starts on `http://localhost:8080` by default.

## Endpoints
The API exposes resources for professionals and contacts. Both resources support text search via `q` and field selection via `fields` (comma-separated list).

### Professionals `/profissionais`
- `GET /profissionais`: lists active professionals with optional `?q=` and `?fields=` filters.
- `GET /profissionais/{id}`: retrieves an active professional by ID.
- `POST /profissionais`: creates a professional. Example payload:
  ```json
  {
    "nome": "Ana Souza",
    "cargo": "Analyst",
    "nascimento": "1990-05-10T00:00:00.000Z",
    "contatos": []
  }
  ```
- `PUT /profissionais/{id}`: updates a professional.
- `DELETE /profissionais/{id}`: marks the professional as inactive (soft delete).

### Contacts `/contatos`
- `GET /contatos`: lists contacts with optional `?q=` and `?fields=` filters.
- `GET /contatos/{id}`: retrieves a contact by ID.
- `POST /contatos`: creates a contact associated with a professional. Example payload:
  ```json
  {
    "nome": "Telefone",
    "contato": "+55 11 91234-5678",
    "profissional": { "nome": "Ana Souza" }
  }
  ```
- `PUT /contatos/{id}`: updates a contact.
- `DELETE /contatos/{id}`: deletes a contact.

## Business rules
- Creation of professionals and contacts avoids duplicates by searching for existing data first.
- Professionals are deactivated instead of being permanently removed.
- Responses can be reduced to a subset of fields requested via the `fields` parameter.

## Tests
Run the automated test suite with:
```bash
./mvnw test
```

The project includes unit and integration coverage:
- Service layer unit tests validating professional and contact management behaviors (creation, updates, soft delete, duplicate checks).
- Repository tests for the JPA adapters that exercise search, filtering, and persistence flows.
- Controller integration tests that start the Spring context and verify API responses for professionals and contacts.
