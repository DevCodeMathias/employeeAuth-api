# employeeAuth

API de autenticacao de funcionarios. A aplicacao permite cadastrar funcionarios, autenticar por CPF e senha, e gerar um bearer token JWT com validade configurada.

## Contexto

Este projeto faz parte de um backend que centraliza autenticacao de funcionarios. O cadastro salva os dados do funcionario no MongoDB, com a senha criptografada usando BCrypt. O login valida as credenciais e retorna um token JWT que pode ser usado por outros servicos para identificar o funcionario autenticado.

Atualmente a API expoe apenas rotas publicas de cadastro, login e healthcheck. A configuracao de seguranca esta preparada para trabalhar de forma stateless, sem sessao HTTP.

## Stack

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Security
- Spring Data MongoDB
- Bean Validation
- JJWT
- Maven Wrapper
- JUnit 6 e Mockito

## Arquitetura

O projeto separa responsabilidades em camadas:

```text
controller
  Recebe requests HTTP e chama os servicos da aplicacao.

application
  Contem DTOs, interfaces de servico e regras de caso de uso.

domain
  Contem entidades, excecoes de negocio, tipos e contratos de repositorio.

infrastructure
  Contem implementacoes tecnicas: MongoDB, JWT, Spring Security e tratamento global de erros.
```

Fluxo do repositorio de funcionarios:

```text
EmployeeService/AuthService
        |
        v
IEmployeeRepository
        |
        v
MongoEmployeeRepository
        |
        v
SpringDataEmployeeMongoRepository
        |
        v
MongoDB
```

- `IEmployeeRepository`: contrato usado pela aplicacao.
- `MongoEmployeeRepository`: adaptador da infraestrutura que implementa o contrato do dominio.
- `SpringDataEmployeeMongoRepository`: interface Spring Data Mongo que fornece operacoes como `save`.

## Configuracao

Arquivo principal:

```text
src/main/resources/application.properties
```

Configuracoes atuais:

```properties
spring.application.name=employeeAuth
server.port=8081
logging.level.com.devBackend.employeeAuth=${APP_LOG_LEVEL:INFO}

spring.mongodb.uri=<mongodb-uri>
mongodb.collections.employees=employessCollection

security.jwt.secret=<jwt-secret>
security.jwt.expiration-hours=24
```

Variaveis que podem ser externalizadas:

| Variavel | Uso | Valor padrao atual |
| --- | --- | --- |
| `APP_LOG_LEVEL` | Nivel de log da aplicacao | `INFO` |
| `spring.mongodb.uri` | URI de conexao MongoDB | definido em `application.properties` |
| `mongodb.collections.employees` | Collection de funcionarios | `employessCollection` |
| `security.jwt.secret` | Chave usada para assinar JWT | definido em `application.properties` |
| `security.jwt.expiration-hours` | Duracao do token em horas | `24` |

Recomendacao: em ambientes reais, nao deixe URI de banco e segredo JWT versionados. Use variaveis de ambiente ou configuracao externa.

## Como executar

Pre-requisitos:

- Java 17 instalado
- Acesso ao MongoDB configurado em `spring.mongodb.uri`

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS:

```bash
./mvnw spring-boot:run
```

A aplicacao sobe em:

```text
http://localhost:8081
```

## Como rodar os testes

No Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Em Linux/macOS:

```bash
./mvnw test
```

## Endpoints

### Healthcheck

```http
GET /api/employees/healthcheck
```

Resposta:

```text
OK
```

### Cadastro de funcionario

```http
POST /api/employees/register
Content-Type: application/json
```

Body:

```json
{
  "name": "Maria",
  "cpf": "52998224725",
  "password": "password123"
}
```

Validacoes:

- `name`: obrigatorio
- `cpf`: obrigatorio e deve ser CPF valido
- `password`: obrigatoria e deve ter no minimo 8 caracteres

Resposta de sucesso:

```http
201 Created
```

```text
usuario criado com sucesso
```

Possiveis erros:

- `400 VALIDATION_ERROR`: dados invalidos
- `409 EMPLOYEE_ALREADY_REGISTERED`: CPF ja cadastrado

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

Body:

```json
{
  "cpf": "52998224725",
  "password": "password123"
}
```

Resposta de sucesso:

```json
{
  "accessToken": "<jwt>"
}
```

O token JWT expira em 24 horas.

Possiveis erros:

- `400 VALIDATION_ERROR`: dados invalidos
- `401 INVALID_CREDENTIALS`: CPF ou senha invalidos

## Formato de erro

Erros sao retornados no formato:

```json
{
  "timestamp": "2026-05-20T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/auth/login",
  "fields": {
    "cpf": "cpf must be a valid CPF document"
  }
}
```

## Exemplos com curl

Healthcheck:

```bash
curl http://localhost:8081/api/employees/healthcheck
```

Cadastro:

```bash
curl -X POST http://localhost:8081/api/employees/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria","cpf":"52998224725","password":"password123"}'
```

Login:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"cpf":"52998224725","password":"password123"}'
```

## Observacoes de seguranca

- A senha do funcionario e criptografada com `BCryptPasswordEncoder`.
- O JWT e assinado com a chave `security.jwt.secret`.
- A aplicacao esta configurada como stateless.
- Atualmente apenas cadastro, login e healthcheck sao permitidos; demais rotas sao negadas.
- Para producao, configure segredos fora do repositorio.
