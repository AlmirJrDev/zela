package br.unasp.zela.repository;

import br.unasp.zela.model.Ocorrencia;
import br.unasp.zela.model.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    Optional<Ocorrencia> findByProtocolo(String protocolo);

    List<Ocorrencia> findByCidadaoIdOrderByDataCriacaoDesc(Long cidadaoId);

    boolean existsByCidadaoId(Long cidadaoId);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsBySecretariaId(Long secretariaId);

    boolean existsByOcorrenciaPrincipalId(Long ocorrenciaPrincipalId);

    @Query("""
            select o from Ocorrencia o
            where (:status is null or o.status = :status)
              and (:categoriaId is null or o.categoria.id = :categoriaId)
              and (:bairro is null or lower(o.bairro) = lower(:bairro))
              and (:equipe is null or lower(o.equipe) = lower(:equipe))
            order by o.dataCriacao desc
            """)
    List<Ocorrencia> filtrar(@Param("status") StatusOcorrencia status,
                             @Param("categoriaId") Long categoriaId,
                             @Param("bairro") String bairro,
                             @Param("equipe") String equipe);
}
