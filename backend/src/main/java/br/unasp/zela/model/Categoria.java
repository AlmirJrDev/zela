package br.unasp.zela.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    /** Prazo de atendimento em dias, usado para calcular o prazo previsto (RF06). */
    @Column(nullable = false)
    private Integer prazoDias;

    public Categoria() {}

    public Categoria(String nome, Integer prazoDias) {
        this.nome = nome;
        this.prazoDias = prazoDias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }
}
