package br.unasp.zela.repository;

import br.unasp.zela.model.Cidadao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {
    Optional<Cidadao> findByEmailIgnoreCase(String email);
    Optional<Cidadao> findByTelefone(String telefone);
    boolean existsByEmailIgnoreCase(String email);
}
