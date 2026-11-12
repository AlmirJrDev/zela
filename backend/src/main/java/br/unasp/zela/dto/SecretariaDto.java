package br.unasp.zela.dto;

import br.unasp.zela.model.Secretaria;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class SecretariaDto {

    private SecretariaDto() {}

    public record Requisicao(
            @NotBlank(message = "nome é obrigatório") @Size(max = 120) String nome,
            @Email(message = "emailContato inválido") String emailContato) {}

    public record Resposta(Long id, String nome, String emailContato) {
        public static Resposta de(Secretaria s) {
            return new Resposta(s.getId(), s.getNome(), s.getEmailContato());
        }
    }
}
