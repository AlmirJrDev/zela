package br.unasp.zela.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** RF06: cada mudança de status fica registrada com data e observação. */
@Entity
@Table(name = "historico_status")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Ocorrencia ocorrencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOcorrencia status;

    @Column(length = 300)
    private String observacao;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public HistoricoStatus() {}

    public HistoricoStatus(Ocorrencia ocorrencia, StatusOcorrencia status, String observacao) {
        this.ocorrencia = ocorrencia;
        this.status = status;
        this.observacao = observacao;
    }

    public Long getId() { return id; }
    public Ocorrencia getOcorrencia() { return ocorrencia; }
    public StatusOcorrencia getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getDataHora() { return dataHora; }
}
