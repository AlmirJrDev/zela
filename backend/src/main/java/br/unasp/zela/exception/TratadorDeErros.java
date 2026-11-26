package br.unasp.zela.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Padroniza o corpo de erro de toda a API. */
@RestControllerAdvice
public class TratadorDeErros {

    public record Erro(LocalDateTime timestamp, int status, String erro, String mensagem, String caminho,
                       Map<String, String> campos) {}

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Erro> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, e.getMessage(), req, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Erro> regra(RegraNegocioException e, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, e.getMessage(), req, null);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Erro> credenciais(CredenciaisInvalidasException e, HttpServletRequest req) {
        return montar(HttpStatus.UNAUTHORIZED, e.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos", req, campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<Erro> requisicaoMalFormada(Exception e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição mal formada: verifique o JSON e os parâmetros", req, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Erro> arquivoGrande(MaxUploadSizeExceededException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "A foto deve ter no máximo 1 MB", req, null);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Erro> semArquivo(MissingServletRequestPartException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Envie a foto no campo 'arquivo'", req, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Erro> argumentoInvalido(IllegalArgumentException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, e.getMessage(), req, null);
    }

    private ResponseEntity<Erro> montar(HttpStatus status, String mensagem, HttpServletRequest req,
                                        Map<String, String> campos) {
        Erro corpo = new Erro(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem,
                req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
