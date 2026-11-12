package br.unasp.zela.exception;

/** Requisição bem formada que fere uma regra do domínio: vira HTTP 409. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
