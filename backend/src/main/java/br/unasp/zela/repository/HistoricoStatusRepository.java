package br.unasp.zela.repository;

import br.unasp.zela.model.HistoricoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {
    List<HistoricoStatus> findByOcorrenciaIdOrderByDataHoraAscIdAsc(Long ocorrenciaId);
    void deleteByOcorrenciaId(Long ocorrenciaId);
}
