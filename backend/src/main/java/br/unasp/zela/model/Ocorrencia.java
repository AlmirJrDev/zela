package br.unasp.zela.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** RF04: número de protocolo gerado ao concluir o registro. */
    @Column(nullable = false, unique = true, length = 20)
    private String protocolo;

    @ManyToOne(optional = false)
    private Categoria categoria;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(length = 300)
    private String fotoUrl;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 200)
    private String endereco;

    @Column(length = 80)
    private String bairro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOcorrencia status = StatusOcorrencia.ABERTA;

    @ManyToOne(optional = false)
    private Cidadao cidadao;

    @ManyToOne
    private Secretaria secretaria;

    @Column(length = 120)
    private String equipe;

    @Column(length = 300)
    private String motivoRecusa;

    /** Quando unida a uma duplicada (RF10), aponta para a ocorrência que segue em atendimento. */
    @ManyToOne
    private Ocorrencia ocorrenciaPrincipal;

    /** RF09: cidadãos que apoiaram esta ocorrência em vez de registrar outra igual. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "apoios",
            joinColumns = @JoinColumn(name = "ocorrencia_id"),
            inverseJoinColumns = @JoinColumn(name = "cidadao_id"))
    private Set<Cidadao> apoiadores = new HashSet<>();

    @Column(length = 300)
    private String fotoConclusaoUrl;

    private Integer avaliacaoNota;

    @Column(length = 300)
    private String avaliacaoComentario;

    private LocalDate prazoPrevisto;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDateTime dataConclusao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
    public Cidadao getCidadao() { return cidadao; }
    public void setCidadao(Cidadao cidadao) { this.cidadao = cidadao; }
    public Secretaria getSecretaria() { return secretaria; }
    public void setSecretaria(Secretaria secretaria) { this.secretaria = secretaria; }
    public String getEquipe() { return equipe; }
    public void setEquipe(String equipe) { this.equipe = equipe; }
    public String getMotivoRecusa() { return motivoRecusa; }
    public void setMotivoRecusa(String motivoRecusa) { this.motivoRecusa = motivoRecusa; }
    public Ocorrencia getOcorrenciaPrincipal() { return ocorrenciaPrincipal; }
    public void setOcorrenciaPrincipal(Ocorrencia ocorrenciaPrincipal) { this.ocorrenciaPrincipal = ocorrenciaPrincipal; }
    public Set<Cidadao> getApoiadores() { return apoiadores; }
    public String getFotoConclusaoUrl() { return fotoConclusaoUrl; }
    public void setFotoConclusaoUrl(String fotoConclusaoUrl) { this.fotoConclusaoUrl = fotoConclusaoUrl; }
    public Integer getAvaliacaoNota() { return avaliacaoNota; }
    public void setAvaliacaoNota(Integer avaliacaoNota) { this.avaliacaoNota = avaliacaoNota; }
    public String getAvaliacaoComentario() { return avaliacaoComentario; }
    public void setAvaliacaoComentario(String avaliacaoComentario) { this.avaliacaoComentario = avaliacaoComentario; }
    public LocalDate getPrazoPrevisto() { return prazoPrevisto; }
    public void setPrazoPrevisto(LocalDate prazoPrevisto) { this.prazoPrevisto = prazoPrevisto; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
