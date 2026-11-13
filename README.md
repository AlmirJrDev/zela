# Zela+ — Zeladoria Urbana Colaborativa

O morador registra um problema da rua com foto e localização; a prefeitura trata, encaminha e devolve o status até a solução.

Projeto da disciplina de Engenharia de Software (UNASP), usado também na atividade de Gerência de Configuração de Software (GCS).

## Partes do sistema

| Pasta | O que é |
| --- | --- |
| `src/app-cidadao/` | App do cidadão: entrar, mapa, registrar, acompanhar e avaliar ocorrências (T01 a T07) |
| `src/app-equipe/` | App da equipe de campo: ordens de serviço e conclusão com foto (T08) |
| `src/painel-web/` | Painel da prefeitura: fila de triagem e indicadores (T09, T10) |
| `src/shared/` | Cliente da API, configuração (status, categorias) e estilos compartilhados |
| `backend/` | API REST em Spring Boot (Projeto Integrador) — veja `backend/README.md` |
| `docs/` | Requisitos e plano de configuração |

## Como abrir

1. Suba a API: na pasta `backend`, rode `mvnw.cmd spring-boot:run` (fica em `http://localhost:8080`).
2. Sirva a pasta `src` por HTTP, por exemplo com `npx http-server src -p 5500`.
3. Abra `http://localhost:5500/app-cidadao/` no navegador e crie uma conta.

## Equipe

| Integrante | Papel |
| --- | --- |
| Almir Junior Araujo Gomes | Desenvolvedor principal |
| João Pedro Silva Soares | Gestor de Configuração |
| Kauã Almeida | Revisor de código |

## Como contribuir

Leia `docs/plano-de-configuracao.md`. Resumo:

- nunca altere a `main` diretamente; crie uma branch `tipo/RFxx-descricao`;
- mensagens de commit no padrão `tipo(escopo): descrição` (Conventional Commits);
- o merge só acontece por pull request aprovado por outra pessoa.
