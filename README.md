# To-Do (CRUD) - Lista de Tarefas - Backend

Projeto avaliativo do 1º bimestre de **Laboratório de Desenvolvimento Multiplataforma** (6º DSM - Fatec Franca).

API REST para o gerenciamento de tarefas do dia a dia. Não possui sistema de login nem o conceito de usuário.

## Tecnologias

- Java 17
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL
- JUnit 5, Mockito e MockMvc (testes rodam em H2 em memória)

## Entidade Tarefa

| Campo           | Tipo                                      | Observação                          |
| --------------- | ----------------------------------------- | ----------------------------------- |
| id              | número                                    | gerado pelo banco                   |
| nome            | texto (até 100)                           | obrigatório                         |
| descricao       | texto (até 500)                           | opcional                            |
| status          | `PENDENTE`, `EM_ANDAMENTO` ou `CONCLUIDA` | padrão `PENDENTE`                   |
| observacoes     | texto (até 500)                           | opcional                            |
| dataCriacao     | data/hora                                 | preenchida automaticamente          |
| dataAtualizacao | data/hora                                 | atualizada a cada alteração         |

## Estrutura

```
database/script.sql                 Script de criação do banco
src/main/java/br/edu/fatec/todo
  model/        Tarefa, StatusTarefa (classes de modelo)
  repository/   TarefaRepository (acesso ao banco)
  service/      TarefaService (regras e serviços)
  controller/   TarefaController (endpoints REST)
  dto/          TarefaRequest (dados de entrada)
  exception/    Tratamento de erros da API
src/test/java/br/edu/fatec/todo
  service/      TarefaServiceTest (testes unitários)
  controller/   TarefaControllerIntegrationTest (testes de integração)
```

## Como executar

### 1. Banco de dados

```bash
psql -U postgres -c "CREATE DATABASE todo_db"
psql -U postgres -d todo_db -f database/script.sql
```

### 2. Conexão

A conexão fica em `src/main/resources/application.properties`. Os valores padrão são
`localhost:5432`, banco `todo_db`, usuário `postgres` e senha `postgres`, e podem ser
trocados pelas variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`.

### 3. Aplicação

```bash
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

A API sobe em `http://localhost:8080`.

### 4. Testes

```bash
./mvnw test
```

Os testes não precisam do PostgreSQL: usam um banco H2 em memória.

## Endpoints

| Método | Rota                 | Descrição                                        |
| ------ | -------------------- | ------------------------------------------------ |
| POST   | `/api/tarefas`       | Cria uma tarefa                                  |
| GET    | `/api/tarefas`       | Lista as tarefas (filtro opcional `?status=...`) |
| GET    | `/api/tarefas/{id}`  | Busca uma tarefa pelo id                         |
| PUT    | `/api/tarefas/{id}`  | Altera uma tarefa                                |
| DELETE | `/api/tarefas/{id}`  | Deleta uma tarefa                                |

Exemplo de corpo para `POST` e `PUT`:

```json
{
  "nome": "Estudar Spring Boot",
  "descricao": "Revisar JPA e REST",
  "status": "EM_ANDAMENTO",
  "observacoes": "Foco em testes"
}
```

Respostas de erro: `400` para dados inválidos (nome vazio, status inexistente) e `404` quando a tarefa não existe.
