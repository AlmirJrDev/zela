# Plano de Configuração — Zela+

O grupo usa GitHub Flow simplificado: a `main` sempre funciona, todo trabalho nasce em uma branch e só entra por pull request revisado.

## Papéis

| Integrante | Papel | Responsabilidade |
| --- | --- | --- |
| Almir Junior Araujo Gomes | Desenvolvedor principal | Desenvolve o código e abre os pull requests |
| João Pedro Silva Soares | Gestor de Configuração | Faz os merges na `main`, cria tags e releases, mantém o CHANGELOG |
| Kauã Almeida | Revisor de código | Revisa e aprova os pull requests antes do merge |

## Itens de configuração

| Item | Onde fica |
| --- | --- |
| Documento de requisitos (RF, RNF, histórias) | `docs/requisitos.md` |
| Plano de configuração | `docs/plano-de-configuracao.md` |
| Protótipo do app do cidadão | `src/app-cidadao/` |
| Protótipo do painel da prefeitura | `src/painel-web/` |
| Dados compartilhados (categorias, status) | `src/shared/config.js` |
| Estilos compartilhados | `src/shared/style.css` |
| Descrição e histórico de versões | `README.md`, `CHANGELOG.md` |

## Convenções de nomeação

- **Branches:** `tipo/RFxx-descricao-curta`, minúsculas e hífen. Ex.: `feature/RF02-registrar-ocorrencia`, `fix/RF05-status-errado`, `docs/atualiza-readme`.
- **Commits** (Conventional Commits): `tipo(escopo): descrição no imperativo`. Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.
  Ex.: `feat(app-cidadao): cria tela de nova ocorrência (RF02)`.
- **Pull requests:** título igual ao commit principal; descrição com o que mudou, o requisito atendido e como testar.
- **Arquivos e pastas:** minúsculas, sem acento, palavras separadas por hífen.

## Política de versionamento

Versionamento semântico `MAIOR.MENOR.CORREÇÃO`, com tags na `main` criadas pela página Releases do GitHub.

| Versão | Conteúdo |
| --- | --- |
| `v1.0.0` | App do cidadão: registrar ocorrência (RF02) e listar as minhas (RF05) |
| `v1.0.x` | Correções sem nova funcionalidade |
| `v2.0.0` | Painel web com fila de triagem (RF10) |

## Política de branching

- `main`: código estável e versionado; protegida, sem push direto.
- `feature/*`, `fix/*`, `docs/*`: uma branch por tarefa, criada a partir da `main` atualizada.
- Merge só por pull request, com pelo menos 1 aprovação; o autor não aprova o próprio PR.
- Branch apagada depois do merge.

## Backup e recuperação

- **Backup:** o GitHub é a cópia principal e o clone local do desenvolvedor é a segunda. Ao fim de cada versão, o Gestor baixa o ZIP do repositório (Code → Download ZIP) e o ZIP fica anexado à release.
- **Voltar a uma versão:** `git checkout v1.0.0`, ou baixar o ZIP da release no GitHub.
- **Desfazer um merge publicado:** botão *Revert* no pull request do GitHub, ou `git revert <hash>`.
- **Branch apagada por engano:** botão *Restore branch* no pull request.
