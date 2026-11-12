# Zela+ API (backend)

API REST do Zela+ em Spring Boot 3.3.5 e Java 17, com banco H2 em memória. Atende aos requisitos funcionais RF01 a RF14 do documento de requisitos.

## Como rodar

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`. Ao iniciar, já cria 4 categorias e 2 secretarias.

- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:zela`, usuário `sa`, sem senha)

## Postman

Importe `postman/zela-api.postman_collection.json` e use *Run collection* na ordem das pastas: cada pasta usa IDs salvos pelas anteriores. São 57 requisições com testes automáticos.

## Endpoints

| Método | Rota | Requisito |
| --- | --- | --- |
| POST | `/api/cidadaos` | RF01 cadastro |
| POST | `/api/auth/login` | RF01 login por e-mail ou telefone |
| GET, PUT, DELETE | `/api/cidadaos`, `/api/cidadaos/{id}` | CRUD do cidadão |
| GET, POST, PUT, DELETE | `/api/categorias`, `/api/categorias/{id}` | CRUD de categorias |
| GET, POST, PUT, DELETE | `/api/secretarias`, `/api/secretarias/{id}` | CRUD de secretarias |
| POST | `/api/ocorrencias` | RF02, RF03, RF04 registrar com protocolo |
| GET | `/api/ocorrencias?status=&categoriaId=&bairro=` | listar e filtrar |
| GET | `/api/ocorrencias/{id}`, `/api/ocorrencias/protocolo/{protocolo}` | consultar |
| PUT, DELETE | `/api/ocorrencias/{id}` | editar e excluir (só ABERTA) |
| GET | `/api/cidadaos/{id}/ocorrencias` | RF05 minhas ocorrências |
| GET | `/api/ocorrencias/{id}/historico` | RF06 histórico e prazo |
| GET | `/api/cidadaos/{id}/notificacoes`, PATCH `/api/notificacoes/{id}/lida` | RF07 notificações |
| GET | `/api/ocorrencias/proximas?lat=&lng=&raioMetros=` | RF08 próximas no mapa |
| POST | `/api/ocorrencias/{id}/apoios` | RF09 apoiar |
| PATCH | `/api/ocorrencias/{id}/triagem` | RF10 VALIDAR, RECATEGORIZAR, UNIR, RECUSAR |
| PATCH | `/api/ocorrencias/{id}/encaminhamento` | RF11 secretaria e equipe |
| PATCH | `/api/ocorrencias/{id}/execucao`, `/api/ocorrencias/{id}/conclusao` | RF12 execução com foto |
| POST | `/api/ocorrencias/{id}/avaliacao` | RF13 avaliação |
| GET | `/api/indicadores` | RF14 indicadores |

## Ciclo de status

`ABERTA → VALIDADA → ENCAMINHADA → EM_EXECUCAO → CONCLUIDA`; na triagem a ocorrência também pode ir para `RECUSADA` (com motivo) ou `UNIDA` (duplicada de outra). Cada mudança grava o histórico e notifica o autor e quem apoiou.

## Erros

Todos os erros têm o mesmo formato (`status`, `erro`, `mensagem`, `caminho` e, em validação, `campos`): 400 dados inválidos, 401 login inválido, 404 não encontrado, 409 regra de negócio.

## Estrutura

```
src/main/java/br/unasp/zela/
  model/        entidades JPA
  repository/   Spring Data JPA
  dto/          requisições e respostas (records)
  service/      regras de negócio
  controller/   endpoints REST
  exception/    tratamento de erros
  config/       dados iniciais e CORS
```
