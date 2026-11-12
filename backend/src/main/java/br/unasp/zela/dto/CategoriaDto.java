package br.unasp.zela.dto;

import br.unasp.zela.model.Categoria;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CategoriaDto {

    private CategoriaDto() {}

    public record Requisicao(
            @NotBlank(message = "nome é obrigatório") @Size(max = 80) String nome,
            @NotNull(message = "prazoDias é obrigatório") @Min(value = 1, message = "prazoDias mínimo é 1")
            @Max(value = 365, message = "prazoDias máximo é 365") Integer prazoDias) {}

    public record Resposta(Long id, String nome, Integer prazoDias) {
        public static Resposta de(Categoria c) {
            return new Resposta(c.getId(), c.getNome(), c.getPrazoDias());
        }
    }
}
