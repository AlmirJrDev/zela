# Changelog

Todas as mudanças relevantes do Zela+ ficam registradas aqui.
O projeto segue o versionamento semântico (`MAIOR.MENOR.CORREÇÃO`).

## [Não lançado]

- Estrutura inicial do repositório.
- API REST em Spring Boot com os requisitos RF01 a RF14 e collection do Postman (`backend/`).
- App do cidadão: tela de entrar e criar conta ligada à API (T01, RF01).
- App do cidadão: mapa de ocorrências próximas e registro em 3 passos com aviso de ocorrência parecida e apoio (T02 a T05; RF02, RF03, RF04, RF08, RF09).
- App do cidadão: minhas ocorrências com avisos de status e detalhe com linha do tempo e avaliação (T06, T07; RF05, RF06, RF07, RF13).
- API e app: envio de foto de verdade, comprimida no celular para até 1 MB (RF02, RNF08).
- Painel da prefeitura: fila de triagem com validar, recategorizar, unir duplicadas, recusar com motivo e encaminhar (T09; RF10, RF11).
