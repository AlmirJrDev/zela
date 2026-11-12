package br.unasp.zela.model;

import jakarta.persistence.*;

@Entity
@Table(name = "secretarias")
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(length = 120)
    private String emailContato;

    public Secretaria() {}

    public Secretaria(String nome, String emailContato) {
        this.nome = nome;
        this.emailContato = emailContato;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
}
