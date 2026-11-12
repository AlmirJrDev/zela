package br.unasp.zela.dto;

import br.unasp.zela.model.HistoricoStatus;
import br.unasp.zela.model.Notificacao;
import br.unasp.zela.model.Ocorrencia;
import br.unasp.zela.model.StatusOcorrencia;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public final class OcorrenciaDto {

    private OcorrenciaDto() {}

    /** RF02/RF03: registro com categoria, descrição, foto e localização. */
    public record Registro(
            @NotNull(message = "cidadaoId é obrigatório") Long cidadaoId,
            @NotNull(message = "categoriaId é obrigatório") Long categoriaId,
            @NotBlank(message = "descricao é obrigatória") @Size(max = 500) String descricao,
            @Size(max = 300) String fotoUrl,
            @NotNull(message = "latitude é obrigatória") @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @NotNull(message = "longitude é obrigatória") @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @Size(max = 200) String endereco,
            @Size(max = 80) String bairro) {}

    /** Edição pelo cidadão enquanto a ocorrência ainda está ABERTA (inclui ajuste do pino, RF03). */
    public record Edicao(
            @NotBlank(message = "descricao é obrigatória") @Size(max = 500) String descricao,
            @Size(max = 300) String fotoUrl,
            @NotNull(message = "latitude é obrigatória") @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @NotNull(message = "longitude é obrigatória") @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @Size(max = 200) String endereco,
            @Size(max = 80) String bairro) {}

    public record Apoio(@NotNull(message = "cidadaoId é obrigatório") Long cidadaoId) {}

    public enum AcaoTriagem { VALIDAR, RECATEGORIZAR, UNIR, RECUSAR }

    /** RF10: triagem pelo atendente. */
    public record Triagem(
            @NotNull(message = "acao é obrigatória (VALIDAR, RECATEGORIZAR, UNIR ou RECUSAR)") AcaoTriagem acao,
            Long categoriaId,
            Long ocorrenciaPrincipalId,
            @Size(max = 300) String motivo) {}

    /** RF11: encaminhamento a uma secretaria e equipe. */
    public record Encaminhamento(
            @NotNull(message = "secretariaId é obrigatório") Long secretariaId,
            @NotBlank(message = "equipe é obrigatória") @Size(max = 120) String equipe) {}

    public record InicioExecucao(@Size(max = 300) String observacao) {}

    /** RF12: execução registrada com foto de conclusão. */
    public record Conclusao(
            @NotBlank(message = "fotoConclusaoUrl é obrigatória") @Size(max = 300) String fotoConclusaoUrl,
            @Size(max = 300) String observacao) {}

    /** RF13: avaliação do atendimento. */
    public record Avaliacao(
            @NotNull(message = "nota é obrigatória") @Min(value = 1, message = "nota mínima é 1")
            @Max(value = 5, message = "nota máxima é 5") Integer nota,
            @Size(max = 300) String comentario) {}

    public record Resposta(
            Long id,
            String protocolo,
            StatusOcorrencia status,
            CategoriaDto.Resposta categoria,
            String descricao,
            String fotoUrl,
            Double latitude,
            Double longitude,
            String endereco,
            String bairro,
            Long cidadaoId,
            String cidadaoNome,
            SecretariaDto.Resposta secretaria,
            String equipe,
            String motivoRecusa,
            String protocoloPrincipal,
            int apoios,
            String fotoConclusaoUrl,
            Integer avaliacaoNota,
            String avaliacaoComentario,
            LocalDate prazoPrevisto,
            LocalDateTime dataCriacao,
            LocalDateTime dataConclusao) {

        public static Resposta de(Ocorrencia o) {
            return new Resposta(
                    o.getId(), o.getProtocolo(), o.getStatus(),
                    CategoriaDto.Resposta.de(o.getCategoria()),
                    o.getDescricao(), o.getFotoUrl(), o.getLatitude(), o.getLongitude(),
                    o.getEndereco(), o.getBairro(),
                    o.getCidadao().getId(), o.getCidadao().getNome(),
                    o.getSecretaria() == null ? null : SecretariaDto.Resposta.de(o.getSecretaria()),
                    o.getEquipe(), o.getMotivoRecusa(),
                    o.getOcorrenciaPrincipal() == null ? null : o.getOcorrenciaPrincipal().getProtocolo(),
                    o.getApoiadores().size(),
                    o.getFotoConclusaoUrl(), o.getAvaliacaoNota(), o.getAvaliacaoComentario(),
                    o.getPrazoPrevisto(), o.getDataCriacao(), o.getDataConclusao());
        }
    }

    public record ProximaResposta(Long id, String protocolo, StatusOcorrencia status, String categoria,
                                  String descricao, int apoios, long distanciaMetros) {}

    public record HistoricoResposta(StatusOcorrencia status, String observacao, LocalDateTime dataHora) {
        public static HistoricoResposta de(HistoricoStatus h) {
            return new HistoricoResposta(h.getStatus(), h.getObservacao(), h.getDataHora());
        }
    }

    /** RF06: histórico de status mais o prazo previsto. */
    public record Acompanhamento(String protocolo, StatusOcorrencia statusAtual, LocalDate prazoPrevisto,
                                 boolean atrasada, java.util.List<HistoricoResposta> historico) {}

    public record NotificacaoResposta(Long id, String protocolo, String mensagem, boolean lida, LocalDateTime dataHora) {
        public static NotificacaoResposta de(Notificacao n) {
            return new NotificacaoResposta(n.getId(), n.getOcorrencia().getProtocolo(), n.getMensagem(),
                    n.isLida(), n.getDataHora());
        }
    }

    /** RF14: indicadores do painel do gestor. */
    public record Indicadores(long total, long concluidas, long atrasadas, Double notaMedia,
                              Map<String, Long> porStatus, Map<String, Long> porCategoria,
                              Map<String, Long> porBairro) {}
}
