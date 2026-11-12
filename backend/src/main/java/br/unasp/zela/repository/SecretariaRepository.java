package br.unasp.zela.repository;

import br.unasp.zela.model.Secretaria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
