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
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

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

As propriedades abaixo fazem rotas inexistentes cairem no `GlobalExceptionHandler`, retornando erro JSON padronizado:

```properties
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

## Como executar

### Pre-requisitos

- Java 17 instalado
- Acesso ao MongoDB configurado em `spring.mongodb.uri`
- Porta `8081` livre

### 1. Conferir configuracao

Antes de subir a aplicacao, confira o arquivo:

```text
src/main/resources/application.properties
```

As configuracoes minimas para execucao local sao:

```properties
server.port=8081
spring.mongodb.uri=<sua-uri-do-mongodb>
mongodb.collections.employees=employessCollection
security.jwt.secret=<uma-chave-com-pelo-menos-32-caracteres>
security.jwt.expiration-hours=24
```

O projeto ja possui valores configurados nesse arquivo, entao basta ajustar se voce quiser usar outro banco, outra collection ou outro segredo JWT.

### 2. Rodar os testes

No Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Em Linux/macOS:

```bash
./mvnw test
```

### 3. Subir a aplicacao

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

A base atual da API e:

```text
http://localhost:8081/api/v1
```

### 4. Validar se subiu

Depois que o terminal indicar que a aplicacao iniciou, teste:

```bash
curl http://localhost:8081/api/v1/employees/healthcheck
```

Resposta esperada:

```text
OK
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
GET /api/v1/employees/healthcheck
```

Resposta:

```text
OK
```

### Cadastro de funcionario

```http
POST /api/v1/employees/register
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
- `cpf`: obrigatorio, deve conter exatamente 11 digitos e nao deve usar pontos ou traco
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
- `404 ROUTE_NOT_FOUND`: rota inexistente

### Login

```http
POST /api/v1/auth/login
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
- `404 ROUTE_NOT_FOUND`: rota inexistente

## Formato de erro

Erros sao retornados no formato:

```json
{
  "timestamp": "2026-05-20T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/auth/login",
  "fields": {
    "cpf": "cpf must contain exactly 11 digits without dots or hyphen"
  }
}
```

### Rota nao encontrada

Quando uma rota nao existe, a API retorna:

```http
404 Not Found
```

```json
{
  "timestamp": "2026-05-21T19:00:00",
  "status": 404,
  "error": "Not Found",
  "code": "ROUTE_NOT_FOUND",
  "message": "Route not found",
  "path": "/api/v1/unknown",
  "fields": null
}
```

## Exemplos com curl

### 1. Healthcheck

```bash
curl http://localhost:8081/api/v1/employees/healthcheck
```

Resposta esperada:

```text
OK
```

### 2. Cadastrar funcionario

O CPF precisa ser valido, deve ser enviado somente com 11 digitos, sem pontos ou traco, e a senha deve ter pelo menos 8 caracteres.

```bash
curl -X POST http://localhost:8081/api/v1/employees/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria","cpf":"52998224725","password":"password123"}'
```

Resposta esperada:

```text
usuario criado com sucesso
```

Se o CPF ja existir, a API retorna `409 EMPLOYEE_ALREADY_REGISTERED`.

### 3. Fazer login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"cpf":"52998224725","password":"password123"}'
```

Resposta esperada:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Esse token expira em 24 horas.

### 4. Exemplo com token em variavel

Em Linux/macOS:

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"cpf":"52998224725","password":"password123"}' \
  | jq -r '.accessToken')
```

No Windows PowerShell:

```powershell
$response = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"cpf":"52998224725","password":"password123"}'

$token = $response.accessToken
```

Atualmente as rotas existentes sao publicas, e a configuracao de seguranca nao exige autenticacao para outras rotas (`anyRequest().permitAll()`). Rotas inexistentes ainda retornam `404 ROUTE_NOT_FOUND` pelo tratamento global de erros. Quando novas rotas protegidas forem adicionadas, ajuste a configuracao de seguranca e use o token no header:

```http
Authorization: Bearer <token>
```

### 5. Testar rota inexistente

```bash
curl -i http://localhost:8081/api/v1/unknown
```

Resposta esperada:

```json
{
  "code": "ROUTE_NOT_FOUND",
  "message": "Route not found",
  "path": "/api/v1/unknown"
}
```

## Observacoes de seguranca

- A senha do funcionario e criptografada com `BCryptPasswordEncoder`.
- O JWT e assinado com a chave `security.jwt.secret`.
- A aplicacao esta configurada como stateless.
- Atualmente cadastro, login e healthcheck sao publicos. A configuracao atual tambem permite qualquer outra rota mapeada, pois usa `anyRequest().permitAll()`.
- Para producao, configure segredos fora do repositorio.
