package br.unasp.zela.dto;

import br.unasp.zela.model.Cidadao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class CidadaoDto {

    private CidadaoDto() {}

    /** RF01: cadastro do cidadão. */
    public record Cadastro(
            @NotBlank(message = "nome é obrigatório") @Size(max = 120) String nome,
            @NotBlank(message = "email é obrigatório") @Email(message = "email inválido") String email,
            @Pattern(regexp = "^\\d{10,11}$", message = "telefone deve ter 10 ou 11 dígitos, só números") String telefone,
            @NotBlank(message = "senha é obrigatória") @Size(min = 6, message = "senha deve ter ao menos 6 caracteres") String senha) {}

    public record Atualizacao(
            @NotBlank(message = "nome é obrigatório") @Size(max = 120) String nome,
            @Pattern(regexp = "^\\d{10,11}$", message = "telefone deve ter 10 ou 11 dígitos, só números") String telefone) {}

    /** RF01: autenticação por e-mail ou telefone. */
    public record Login(
            @NotBlank(message = "login é obrigatório (e-mail ou telefone)") String login,
            @NotBlank(message = "senha é obrigatória") String senha) {}

    public record Resposta(Long id, String nome, String email, String telefone, LocalDateTime dataCadastro) {
        public static Resposta de(Cidadao c) {
            return new Resposta(c.getId(), c.getNome(), c.getEmail(), c.getTelefone(), c.getDataCadastro());
        }
    }

    public record LoginResposta(String mensagem, Resposta cidadao) {}
}
