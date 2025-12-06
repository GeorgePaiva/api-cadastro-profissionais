# API de Cadastro de Profissionais

API REST em Java 17 com Spring Boot para gerenciar profissionais e seus contatos.

## Requisitos
- Java 17+
- Maven 3+
- PostgreSQL (configuração padrão usa database `cadpro` em `localhost:5432` com usuário/senha `postgres`)

## Configuração
As propriedades padrão de conexão e JPA estão em `src/main/resources/application.properties`. Ajuste conforme necessário para seu ambiente de banco de dados.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cadpro
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

## Executando a aplicação
1. Instale dependências e rode a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
2. A API sobe por padrão em `http://localhost:8080`.

## Endpoints
A API expõe recursos para profissionais e contatos. Em ambos é possível filtrar via `q` (busca textual) e limitar campos retornados com `fields` (lista separada por vírgula).

### Profissionais `/profissionais`
- `GET /profissionais`: lista profissionais ativos, com suporte a `?q=` e `?fields=`.
- `GET /profissionais/{id}`: busca profissional ativo por ID.
- `POST /profissionais`: cria profissional. Exemplo de payload:
  ```json
  {
    "nome": "Ana Souza",
    "cargo": "Analista",
    "nascimento": "1990-05-10T00:00:00.000Z",
    "contatos": []
  }
  ```
- `PUT /profissionais/{id}`: atualiza dados do profissional.
- `DELETE /profissionais/{id}`: marca o profissional como inativo (soft delete).

### Contatos `/contatos`
- `GET /contatos`: lista contatos com suporte a `?q=` e `?fields=`.
- `GET /contatos/{id}`: busca contato por ID.
- `POST /contatos`: cria contato associado a um profissional. Exemplo de payload:
  ```json
  {
    "nome": "Telefone",
    "contato": "+55 11 91234-5678",
    "profissional": { "nome": "Ana Souza" }
  }
  ```
- `PUT /contatos/{id}`: atualiza contato existente.
- `DELETE /contatos/{id}`: exclui contato.

## Comportamento de negócio
- Criação de profissionais e contatos evita duplicidade usando buscas por dados existentes.
- Profissionais são desativados em exclusão em vez de removidos definitivamente.
- Respostas podem ser reduzidas ao subconjunto de campos solicitado em `fields`.

## Testes
Execute a suíte de testes automatizados com:
```bash
./mvnw test
```
