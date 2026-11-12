package br.unasp.zela.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** RF07: notificação gerada para o cidadão a cada mudança de status. */
@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cidadao cidadao;

    @ManyToOne(optional = false)
    private Ocorrencia ocorrencia;

    @Column(nullable = false, length = 300)
    private String mensagem;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public Notificacao() {}

    public Notificacao(Cidadao cidadao, Ocorrencia ocorrencia, String mensagem) {
        this.cidadao = cidadao;
        this.ocorrencia = ocorrencia;
        this.mensagem = mensagem;
    }

    public Long getId() { return id; }
    public Cidadao getCidadao() { return cidadao; }
    public Ocorrencia getOcorrencia() { return ocorrencia; }
    public String getMensagem() { return mensagem; }
    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }
    public LocalDateTime getDataHora() { return dataHora; }
}
