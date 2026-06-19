# API Pao de Mel & Cia

Backend inicial em Java com Spring Boot para o sistema Pao de Mel & Cia.

## Requisitos

- Java 17+
- Maven 3.9+
- PostgreSQL 18 rodando localmente
- Database `Paoemel`

## Banco de dados

A API usa PostgreSQL via Spring Data JPA.

Configuracao padrao local:

```properties
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/Paoemel}
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD:1234}
```

Para usar o RDS da AWS, defina as variaveis de ambiente antes de iniciar:

```powershell
$env:DATABASE_URL='jdbc:postgresql://paoemelmetabase.cxa2yiwgqlhd.us-east-2.rds.amazonaws.com:5432/postgres?sslmode=verify-full&sslrootcert=../global-bundle.pem'
$env:DATABASE_USERNAME='paoemel'
$env:DATABASE_PASSWORD='SUA_SENHA_RDS'
mvn spring-boot:run
```

Na primeira execucao, o Hibernate cria/atualiza as tabelas automaticamente e a API insere dados iniciais em:

- `usuarios`
- `encomendas`
- `insumos`
- `fornadas`
- `vendas`

## Como executar

### Resumo rápido (computador local)

| O quê | Onde |
|-------|------|
| Pasta para rodar o comando | `projetopass\api` |
| Comando para iniciar | `mvn spring-boot:run` |
| Endereço no navegador | **`http://localhost:8080`** |
| Porta padrão | **`8080`** |

### Passo a passo

```powershell
cd api
mvn spring-boot:run
```

Quando o terminal mostrar que a aplicacao iniciou, abra no navegador:

```text
http://localhost:8080
```

A porta e configurada em `src/main/resources/application.properties`:

```properties
server.port=${PORT:8080}
```

Ou seja: usa a porta **8080** por padrao. Para mudar (ex.: porta 9090):

```powershell
$env:PORT="9090"
mvn spring-boot:run
```

Depois acesse `http://localhost:9090`.

### Portas usadas no computador local

| Servico | Porta | Endereco |
|---------|-------|----------|
| Sistema web + API (Spring Boot) | **8080** (padrao) | `http://localhost:8080` |
| Banco PostgreSQL | **5432** (padrao) | `localhost:5432` — banco `Paoemel` |

O frontend do sistema fica embutido em `src/main/resources/static/` e e servido pela propria API na raiz (`/`). Login e cadastro ja chamam `/api/auth/login` e `/api/auth/register`.

## Deploy unificado (Railway)

1. Publique o repositorio `paodemel-api` no Railway.
2. Configure as variaveis:
   - `DATABASE_URL` no formato JDBC, por exemplo `jdbc:postgresql://host:porta/railway`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`
   - `PORT` e definido automaticamente pelo Railway
3. Acesse a aplicacao completa pela URL do servico, por exemplo `https://paodemel-api-production.up.railway.app/`.

## Autenticacao

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "login": "gerente@paodemel.com",
  "senha": "12345678",
  "perfil": "GERENTE"
}
```

Perfis aceitos:

- `GERENTE`
- `ATENDENTE`
- `CONFEITEIRO`
- `CLIENTE`

### Cadastro

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "nome": "Maria Oliveira",
  "telefone": "(11) 99999-0000",
  "email": "maria@email.com",
  "perfil": "CLIENTE",
  "senha": "12345678",
  "confirmarSenha": "12345678",
  "codigoInterno": null
}
```

Para `GERENTE`, `ATENDENTE` e `CONFEITEIRO`, envie `codigoInterno`.

## Controle de acesso

As rotas de relatorios e administracao exigem o header:

```http
X-Perfil: GERENTE
```

Se outro perfil tentar acessar, a API retorna `403`.

## Estrutura do projeto

```
com.paodemel.api/
├── controller/     # Rotas REST (@GetMapping, @PostMapping)
├── service/        # Regras de negocio
├── repository/     # Acesso ao banco (JPA)
├── model/          # Entidades do banco
├── dto/            # Objetos de entrada e saida da API
├── exception/      # Tratamento de erros
└── config/         # CORS, banco, dados iniciais
```

Guia para o usuario final: [`GUIA_DO_CLIENTE.md`](GUIA_DO_CLIENTE.md)

## Endpoints principais

- `GET /api/dashboard` — KPIs do painel principal
- `GET /api/encomendas`
- `POST /api/encomendas`
- `GET /api/fornadas`
- `POST /api/fornadas`
- `GET /api/producao`
- `GET /api/estoque`
- `POST /api/vendas`
- `GET /api/perfil`
- `GET /api/relatorios` somente gerente
- `GET /api/admin` somente gerente

## Observacao

Esta versao ja persiste dados no PostgreSQL `Paoemel`. O proximo passo natural e evoluir autenticação com hash de senha e JWT.
