# Requisitos — Zela+

Resumo do documento de Engenharia de Requisitos do grupo.

## Atores

| Ator | O que faz no sistema |
| --- | --- |
| Cidadão | Registra ocorrências, acompanha status, apoia ocorrências de vizinhos |
| Atendente (triagem) | Valida, classifica, une duplicadas e encaminha à secretaria |
| Equipe de campo | Recebe ordens de serviço e registra a execução com foto |
| Gestor | Acompanha indicadores, prazos e mapa de ocorrências |

## Requisitos funcionais

| ID | Requisito | Prioridade |
| --- | --- | --- |
| RF01 | Cadastrar e autenticar o cidadão por e-mail ou telefone | Essencial |
| RF02 | Registrar ocorrência com categoria, descrição, foto e localização | Essencial |
| RF03 | Capturar a localização pelo GPS ou permitir ajuste do pino no mapa | Essencial |
| RF04 | Gerar número de protocolo ao concluir o registro | Essencial |
| RF05 | Listar as ocorrências do cidadão com o status atual | Essencial |
| RF06 | Exibir o histórico de status e o prazo previsto de uma ocorrência | Essencial |
| RF07 | Enviar notificação a cada mudança de status | Importante |
| RF08 | Mostrar no mapa ocorrências próximas já registradas | Importante |
| RF09 | Permitir apoiar uma ocorrência existente em vez de duplicar | Importante |
| RF10 | Triar ocorrência: validar, recategorizar, unir duplicadas ou recusar com motivo | Essencial |
| RF11 | Encaminhar ocorrência a uma secretaria e equipe | Essencial |
| RF12 | Registrar a execução com foto de conclusão | Essencial |
| RF13 | Avaliar o atendimento após a conclusão | Desejável |
| RF14 | Exibir painel com indicadores por bairro, categoria e prazo | Importante |

## Requisitos não funcionais

| ID | Requisito | Categoria |
| --- | --- | --- |
| RNF01 | App em Android 10+ e iOS 15+; painel nos navegadores atuais | Portabilidade |
| RNF02 | Registro de ocorrência em no máximo 3 telas | Usabilidade |
| RNF03 | Dados pessoais conforme a LGPD | Segurança / Legal |
| RNF04 | Senhas com hash e comunicação só por HTTPS | Segurança |
| RNF05 | Salvar o registro sem internet e enviar depois | Confiabilidade |
| RNF06 | Telas principais em até 3 segundos em 4G | Desempenho |
| RNF07 | Contraste e fonte conforme WCAG 2.1 AA | Acessibilidade |
| RNF08 | Fotos comprimidas para no máximo 1 MB | Desempenho |

## Escopo do protótipo neste repositório

| Versão | Requisitos implementados |
| --- | --- |
| v1.0.0 | RF02, RF04, RF05 |
| v2.0.0 | RF06, RF10 |
