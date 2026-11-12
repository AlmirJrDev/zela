package br.unasp.zela.model;

/** Ciclo de vida da ocorrência (7 estados), conforme a especificação de requisitos. */
public enum StatusOcorrencia {
    ABERTA,
    VALIDADA,
    ENCAMINHADA,
    EM_EXECUCAO,
    CONCLUIDA,
    RECUSADA,
    UNIDA
}
