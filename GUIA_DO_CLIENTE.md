# Guia do Cliente — Pão de Mel & Cia

Este guia explica como usar o sistema web da confeitaria **Pão de Mel & Cia**. O sistema reúne encomendas de bolos, produção, fornadas de pães, estoque e vendas em um único painel.

---

## Onde abrir o sistema (leia primeiro)

Existem **duas formas** de usar o Pão de Mel & Cia. Escolha a que se aplica ao seu caso:

### Opção A — Usar pela internet (recomendado para o dia a dia)

Não precisa instalar nada no computador. Basta abrir o navegador (Chrome, Edge, Firefox) e digitar:

```text
https://paodemel-api-production.up.railway.app/
```

| Item | Valor |
|------|-------|
| **Endereço no navegador** | `https://paodemel-api-production.up.railway.app/` |
| **Porta** | Não precisa informar — a porta já vem na URL (`443`, padrão HTTPS) |
| **Quem roda o sistema** | Servidor na nuvem (Railway) |

---

### Opção B — Rodar no computador local (loja, laboratório ou teste)

Use esta opção quando o sistema for executado **no próprio PC** (por exemplo, na confeitaria, sem internet ou em demonstração acadêmica).

#### Passo a passo para iniciar

1. Abra o **Prompt de Comando** ou **PowerShell**.
2. Entre na pasta do projeto:

```powershell
cd caminho\para\projetopass\api
```

> Exemplo no Windows: `cd C:\Users\SeuNome\Documentos\projetopass\api`

3. Inicie o sistema com:

```powershell
mvn spring-boot:run
```

4. Aguarde aparecer no terminal uma mensagem parecida com: `Started PaoDeMelApiApplication`.
5. Abra o navegador e acesse **exatamente** este endereço:

```text
http://localhost:8080
```

| Item | Valor |
|------|-------|
| **Endereço no navegador** | `http://localhost:8080` |
| **Porta padrão** | **8080** |
| **Host** | `localhost` (significa “este computador”) |
| **Tela de login** | `http://localhost:8080/` (raiz do site) |
| **API (uso técnico)** | `http://localhost:8080/api/...` |

#### Se a porta 8080 estiver ocupada

Outro programa pode estar usando a porta 8080. Nesse caso, defina outra porta **antes** de rodar:

```powershell
$env:PORT="9090"
mvn spring-boot:run
```

E abra no navegador:

```text
http://localhost:9090
```

#### Requisitos no computador local

| Requisito | Versão mínima |
|-----------|---------------|
| Java | 17 ou superior |
| Maven | 3.9 ou superior |
| PostgreSQL | Banco `Paoemel` na porta **5432** (padrão do PostgreSQL) |
| Navegador | Chrome, Edge ou Firefox atualizado |

> **Importante:** o frontend (telas) e a API rodam **juntos** na mesma porta. Não é preciso abrir outra porta para a interface — tudo fica em `http://localhost:8080`.

---

## 1. Como acessar (depois que o sistema estiver rodando)

1. Abra o endereço correto no navegador:
   - **Internet:** `https://paodemel-api-production.up.railway.app/`
   - **Computador local:** `http://localhost:8080`
2. Na tela inicial, faça **login** ou **cadastre-se**.
3. Após entrar, você verá o **Dashboard** com os indicadores do dia.

---

## 2. Perfis de acesso

Cada usuário possui um perfil. Escolha o perfil correto no login — ele define quais telas você pode usar.

| Perfil | Para quem é | O que pode fazer |
|--------|-------------|------------------|
| **Gerente** | Dono(a) ou responsável pela loja | Acesso total: dashboard, encomendas, produção, fornadas, estoque, vendas, relatórios e administração |
| **Atendente** | Balcão / recepção | Dashboard, encomendas, nova encomenda, estoque e vendas de balcão |
| **Confeiteiro** | Cozinha / produção de bolos | Dashboard, encomendas, produção e estoque |
| **Cliente** | Cliente final | Dashboard, nova encomenda e acompanhamento de pedidos |

### Usuários de demonstração (senha: `12345678`)

| E-mail | Perfil | Código interno |
|--------|--------|----------------|
| `gerente@paodemel.com` | Gerente | GER-001 |
| `atendente@paodemel.com` | Atendente | ATE-001 |
| `confeiteiro@paodemel.com` | Confeiteiro | CON-001 |
| `cliente@email.com` | Cliente | — |

---

## 3. Login e cadastro

### Login
1. Informe seu **e-mail** (ou login).
2. Digite sua **senha**.
3. Selecione o **perfil** cadastrado.
4. Clique em **Entrar**.

### Cadastro de cliente
1. Clique em **Criar conta**.
2. Preencha nome, telefone, e-mail e senha.
3. Selecione perfil **Cliente** e complete CPF, endereço e data de nascimento.
4. Clique em **Criar conta**.

### Cadastro da equipe (Gerente, Atendente, Confeiteiro)
- Além dos dados básicos, informe o **código interno** fornecido pela loja (ex.: GER-001).

---

## 4. Telas do sistema

### Dashboard
Mostra indicadores atualizados pela API:
- Encomendas do dia
- Bolos em produção
- Pães disponíveis (soma das fornadas)
- Itens com estoque baixo
- Pedidos entregues

Os números são carregados automaticamente ao abrir esta tela.

### Encomendas
Lista todas as encomendas de bolos com código, cliente, massa, recheio, data de entrega e status.

### Nova encomenda
Assistente em 3 passos:
1. **Cliente** — nome (preenchido automaticamente se você estiver logado).
2. **Produto** — massa e recheio.
3. **Entrega** — data e hora.

Clique em **Salvar encomenda**. O pedido aparece na lista de encomendas e atualiza o dashboard.

### Produção
Quadro visual com etapas da produção (Aguardando, Preparando massa, Recheando, Decorando, Finalizado).

### Fornadas
Registre a produção diária de pães:
1. Tipo de pão
2. Quantidade produzida
3. Hora de saída

Clique em **Registrar Fornada**. A lista abaixo mostra as fornadas salvas.

### Estoque
Tabela de insumos com quantidade atual, unidade, estoque mínimo e indicador (Normal, Atenção ou Crítico). Dados carregados da API.

### Vendas de balcão (PDV)
Tela de ponto de venda para registrar vendas rápidas no balcão.

### Relatórios *(somente Gerente)*
Receita, lucro estimado e rankings de produtos. Acesso restrito — outros perfis não veem esta tela.

### Administração *(somente Gerente)*
Visão dos perfis e permissões do sistema.

### Perfil
Mostra seu nome, e-mail, perfil ativo e permissões da conta.

---

## 5. Status das encomendas

| Status | Significado |
|--------|-------------|
| Aguardando Produção | Pedido registrado, ainda não iniciado |
| Em Produção | Bolo sendo preparado |
| Pronto | Aguardando retirada ou entrega |
| Entregue | Pedido concluído |

---

## 6. Dicas de uso

- **Sempre selecione o perfil correto** no login. Perfil errado impede o acesso.
- Após salvar encomenda ou fornada, o **dashboard é atualizado** automaticamente.
- Se aparecer mensagem de erro, leia o texto — a API informa o que falta (campo vazio, senha incorreta, etc.).
- Use **Sair** no menu para encerrar a sessão em computadores compartilhados.

---

## 7. Problemas comuns

| Problema | Solução |
|----------|---------|
| Página não abre / "Não é possível acessar" | Confirme se o sistema está rodando (`mvn spring-boot:run` na pasta `api`) e use **`http://localhost:8080`** |
| Erro de porta ocupada no terminal | Feche o programa que usa a 8080 ou rode com `$env:PORT="9090"` e acesse `http://localhost:9090` |
| "E-mail não cadastrado" | Verifique o e-mail ou crie uma conta |
| "Senha incorreta" | Confira a senha (mínimo 8 caracteres na demo) |
| "Perfil de acesso incorreto" | Selecione o perfil que foi usado no cadastro |
| "Acesso permitido apenas para Gerente" | Relatórios e Administração exigem perfil Gerente |
| Tela em branco ou números "-" | Verifique se o sistema está no ar; na internet, confira a conexão e recarregue a página |

---

## 8. Suporte

Equipe do projeto **Pão de Mel & Cia**:
- Bruno Caua
- Matheus Oliveira
- Luiza de Deus

Para dúvidas técnicas sobre a API, consulte também o arquivo `README.md` na pasta `api/`.

---

*Última atualização: junho de 2026*
