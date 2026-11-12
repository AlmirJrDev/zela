package br.unasp.zela.repository;

import br.unasp.zela.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByCidadaoIdOrderByDataHoraDescIdDesc(Long cidadaoId);
    void deleteByOcorrenciaId(Long ocorrenciaId);
}
