# API Pao de Mel & Cia

Backend inicial em Java com Spring Boot para o sistema Pao de Mel & Cia.

## Requisitos

- Java 17+
- Maven 3.9+

## Como executar

```bash
cd api
mvn spring-boot:run
```

A API sobe em:

```text
http://localhost:8080
```

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

## Endpoints principais

- `GET /api/dashboard`
- `GET /api/encomendas`
- `POST /api/encomendas`
- `GET /api/producao`
- `POST /api/fornadas`
- `GET /api/estoque`
- `POST /api/vendas`
- `GET /api/perfil`
- `GET /api/relatorios` somente gerente
- `GET /api/admin` somente gerente

## Observacao

Esta primeira versao usa dados em memoria para acelerar o prototipo. O proximo passo natural e adicionar banco de dados com Spring Data JPA e persistir usuarios, encomendas, estoque e vendas.
