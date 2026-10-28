# Zela+ — Zeladoria Urbana Colaborativa

O morador registra um problema da rua com foto e localização; a prefeitura trata, encaminha e devolve o status até a solução.

Projeto da disciplina de Engenharia de Software (UNASP), usado também na atividade de Gerência de Configuração de Software (GCS).

## Partes do sistema

| Pasta | O que é |
| --- | --- |
| `src/app-cidadao/` | Protótipo do app do cidadão: registrar e acompanhar ocorrências |
| `src/painel-web/` | Protótipo do painel da prefeitura: triagem e indicadores |
| `src/shared/` | Dados e estilos compartilhados (categorias, status) |
| `docs/` | Requisitos e plano de configuração |

## Como abrir

Não precisa instalar nada: abra `src/app-cidadao/index.html` no navegador.

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
