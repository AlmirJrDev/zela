package br.unasp.zela.exception;

/** Vira HTTP 401. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Login ou senha inválidos");
    }
}
