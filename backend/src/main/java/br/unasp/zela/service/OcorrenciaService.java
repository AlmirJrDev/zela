package br.unasp.zela.service;

import br.unasp.zela.dto.OcorrenciaDto;
import br.unasp.zela.exception.RecursoNaoEncontradoException;
import br.unasp.zela.exception.RegraNegocioException;
import br.unasp.zela.model.*;
import br.unasp.zela.repository.HistoricoStatusRepository;
import br.unasp.zela.repository.NotificacaoRepository;
import br.unasp.zela.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.unasp.zela.model.StatusOcorrencia.*;

@Service
public class OcorrenciaService {

    private static final Set<StatusOcorrencia> FINALIZADAS = EnumSet.of(CONCLUIDA, RECUSADA, UNIDA);

    private final OcorrenciaRepository repo;
    private final HistoricoStatusRepository historicos;
    private final NotificacaoRepository notificacoes;
    private final CidadaoService cidadaos;
    private final CategoriaService categorias;
    private final SecretariaService secretarias;

    public OcorrenciaService(OcorrenciaRepository repo, HistoricoStatusRepository historicos,
                             NotificacaoRepository notificacoes, CidadaoService cidadaos,
                             CategoriaService categorias, SecretariaService secretarias) {
        this.repo = repo;
        this.historicos = historicos;
        this.notificacoes = notificacoes;
        this.cidadaos = cidadaos;
        this.categorias = categorias;
        this.secretarias = secretarias;
    }

    // ---------- Cidadão ----------

    /** RF02, RF03, RF04: registra e devolve com protocolo e prazo previsto. */
    @Transactional
    public Ocorrencia registrar(OcorrenciaDto.Registro dto) {
        Cidadao cidadao = cidadaos.buscar(dto.cidadaoId());
        Categoria categoria = categorias.buscar(dto.categoriaId());

        Ocorrencia o = new Ocorrencia();
        o.setCidadao(cidadao);
        o.setCategoria(categoria);
        o.setDescricao(dto.descricao().trim());
        o.setFotoUrl(dto.fotoUrl());
        o.setLatitude(dto.latitude());
        o.setLongitude(dto.longitude());
        o.setEndereco(dto.endereco());
        o.setBairro(dto.bairro());
        o.setStatus(ABERTA);
        o.setPrazoPrevisto(LocalDate.now().plusDays(categoria.getPrazoDias()));
        o.setProtocolo("TMP-" + UUID.randomUUID().toString().substring(0, 12));
        repo.save(o);

        o.setProtocolo(String.format("ZELA-%d-%06d", LocalDate.now().getYear(), o.getId()));
        registrarMudanca(o, ABERTA, "Ocorrência registrada pelo cidadão");
        return o;
    }

    @Transactional(readOnly = true)
    public List<Ocorrencia> listar(StatusOcorrencia status, Long categoriaId, String bairro) {
        return repo.filtrar(status, categoriaId, (bairro == null || bairro.isBlank()) ? null : bairro.trim());
    }

    /** RF05: ocorrências do cidadão com o status atual. */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listarDoCidadao(Long cidadaoId) {
        cidadaos.buscar(cidadaoId);
        return repo.findByCidadaoIdOrderByDataCriacaoDesc(cidadaoId);
    }

    @Transactional(readOnly = true)
    public Ocorrencia buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }

    @Transactional(readOnly = true)
    public Ocorrencia buscarPorProtocolo(String protocolo) {
        return repo.findByProtocolo(protocolo.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência com protocolo", protocolo));
    }

    /** RF06: histórico de status e prazo previsto. */
    @Transactional(readOnly = true)
    public OcorrenciaDto.Acompanhamento acompanhar(Long id) {
        Ocorrencia o = buscar(id);
        List<OcorrenciaDto.HistoricoResposta> hist = historicos.findByOcorrenciaIdOrderByDataHoraAscIdAsc(id)
                .stream().map(OcorrenciaDto.HistoricoResposta::de).toList();
        return new OcorrenciaDto.Acompanhamento(o.getProtocolo(), o.getStatus(), o.getPrazoPrevisto(),
                estaAtrasada(o), hist);
    }

    @Transactional
    public Ocorrencia editar(Long id, OcorrenciaDto.Edicao dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "editar", ABERTA);
        o.setDescricao(dto.descricao().trim());
        o.setFotoUrl(dto.fotoUrl());
        o.setLatitude(dto.latitude());
        o.setLongitude(dto.longitude());
        o.setEndereco(dto.endereco());
        o.setBairro(dto.bairro());
        return o;
    }

    @Transactional
    public void excluir(Long id) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "excluir", ABERTA);
        if (repo.existsByOcorrenciaPrincipalId(id)) {
            throw new RegraNegocioException("Outras ocorrências foram unidas a esta; ela não pode ser excluída");
        }
        notificacoes.deleteByOcorrenciaId(id);
        historicos.deleteByOcorrenciaId(id);
        repo.delete(o);
    }

    /** RF08: ocorrências próximas ainda em atendimento, da mais perto para a mais longe. */
    @Transactional(readOnly = true)
    public List<OcorrenciaDto.ProximaResposta> proximas(double lat, double lng, int raioMetros) {
        if (raioMetros < 1 || raioMetros > 10_000) {
            throw new IllegalArgumentException("raioMetros deve estar entre 1 e 10000");
        }
        return repo.findAll().stream()
                .filter(o -> !FINALIZADAS.contains(o.getStatus()))
                .map(o -> new OcorrenciaDto.ProximaResposta(o.getId(), o.getProtocolo(), o.getStatus(),
                        o.getCategoria().getNome(), o.getDescricao(), o.getApoiadores().size(),
                        Math.round(distanciaMetros(lat, lng, o.getLatitude(), o.getLongitude()))))
                .filter(p -> p.distanciaMetros() <= raioMetros)
                .sorted(Comparator.comparingLong(OcorrenciaDto.ProximaResposta::distanciaMetros))
                .toList();
    }

    /** RF09: apoiar uma ocorrência existente em vez de registrar outra igual. */
    @Transactional
    public Ocorrencia apoiar(Long id, Long cidadaoId) {
        Ocorrencia o = buscar(id);
        Cidadao c = cidadaos.buscar(cidadaoId);
        if (FINALIZADAS.contains(o.getStatus())) {
            throw new RegraNegocioException("Ocorrência " + o.getStatus() + " não recebe mais apoios");
        }
        if (o.getCidadao().getId().equals(cidadaoId)) {
            throw new RegraNegocioException("O autor da ocorrência não pode apoiá-la");
        }
        if (!o.getApoiadores().add(c)) {
            throw new RegraNegocioException("Este cidadão já apoia a ocorrência");
        }
        return o;
    }

    /** RF13: avaliação após a conclusão. */
    @Transactional
    public Ocorrencia avaliar(Long id, OcorrenciaDto.Avaliacao dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "avaliar", CONCLUIDA);
        if (o.getAvaliacaoNota() != null) {
            throw new RegraNegocioException("Ocorrência já foi avaliada");
        }
        o.setAvaliacaoNota(dto.nota());
        o.setAvaliacaoComentario(dto.comentario());
        return o;
    }

    // ---------- Prefeitura ----------

    /** RF10: triagem — validar, recategorizar, unir duplicadas ou recusar com motivo. */
    @Transactional
    public Ocorrencia triar(Long id, OcorrenciaDto.Triagem dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "triar", ABERTA, VALIDADA);

        switch (dto.acao()) {
            case VALIDAR -> {
                exigirStatus(o, "validar", ABERTA);
                registrarMudanca(o, VALIDADA, "Ocorrência validada na triagem");
            }
            case RECATEGORIZAR -> {
                if (dto.categoriaId() == null) {
                    throw new IllegalArgumentException("categoriaId é obrigatório para RECATEGORIZAR");
                }
                Categoria nova = categorias.buscar(dto.categoriaId());
                String anterior = o.getCategoria().getNome();
                o.setCategoria(nova);
                o.setPrazoPrevisto(o.getDataCriacao().toLocalDate().plusDays(nova.getPrazoDias()));
                registrarMudanca(o, VALIDADA,
                        "Recategorizada de \"" + anterior + "\" para \"" + nova.getNome() + "\"");
            }
            case UNIR -> {
                if (dto.ocorrenciaPrincipalId() == null) {
                    throw new IllegalArgumentException("ocorrenciaPrincipalId é obrigatório para UNIR");
                }
                if (dto.ocorrenciaPrincipalId().equals(id)) {
                    throw new RegraNegocioException("Uma ocorrência não pode ser unida a ela mesma");
                }
                Ocorrencia principal = buscar(dto.ocorrenciaPrincipalId());
                if (FINALIZADAS.contains(principal.getStatus())) {
                    throw new RegraNegocioException("A ocorrência principal está " + principal.getStatus());
                }
                o.setOcorrenciaPrincipal(principal);
                if (!principal.getCidadao().getId().equals(o.getCidadao().getId())) {
                    principal.getApoiadores().add(o.getCidadao());
                }
                o.getApoiadores().stream()
                        .filter(a -> !a.getId().equals(principal.getCidadao().getId()))
                        .forEach(principal.getApoiadores()::add);
                registrarMudanca(o, UNIDA, "Duplicada: unida à ocorrência " + principal.getProtocolo());
            }
            case RECUSAR -> {
                if (dto.motivo() == null || dto.motivo().isBlank()) {
                    throw new RegraNegocioException("Informe o motivo da recusa");
                }
                o.setMotivoRecusa(dto.motivo().trim());
                registrarMudanca(o, RECUSADA, "Recusada: " + dto.motivo().trim());
            }
        }
        return o;
    }

    /** RF11: encaminhar a uma secretaria e equipe. */
    @Transactional
    public Ocorrencia encaminhar(Long id, OcorrenciaDto.Encaminhamento dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "encaminhar", VALIDADA, ENCAMINHADA);
        Secretaria s = secretarias.buscar(dto.secretariaId());
        o.setSecretaria(s);
        o.setEquipe(dto.equipe().trim());
        registrarMudanca(o, ENCAMINHADA, "Encaminhada para " + s.getNome() + " — equipe " + dto.equipe().trim());
        return o;
    }

    @Transactional
    public Ocorrencia iniciarExecucao(Long id, OcorrenciaDto.InicioExecucao dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "iniciar a execução de", ENCAMINHADA);
        String obs = (dto == null || dto.observacao() == null || dto.observacao().isBlank())
                ? "Equipe iniciou o serviço" : dto.observacao().trim();
        registrarMudanca(o, EM_EXECUCAO, obs);
        return o;
    }

    /** RF12: registrar a execução com foto de conclusão. */
    @Transactional
    public Ocorrencia concluir(Long id, OcorrenciaDto.Conclusao dto) {
        Ocorrencia o = buscar(id);
        exigirStatus(o, "concluir", EM_EXECUCAO);
        o.setFotoConclusaoUrl(dto.fotoConclusaoUrl());
        o.setDataConclusao(LocalDateTime.now());
        String obs = (dto.observacao() == null || dto.observacao().isBlank())
                ? "Serviço concluído" : dto.observacao().trim();
        registrarMudanca(o, CONCLUIDA, obs);
        return o;
    }

    /** RF14: indicadores por status, categoria e bairro, mais prazo e satisfação. */
    @Transactional(readOnly = true)
    public OcorrenciaDto.Indicadores indicadores() {
        List<Ocorrencia> todas = repo.findAll();
        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (StatusOcorrencia s : StatusOcorrencia.values()) {
            porStatus.put(s.name(), todas.stream().filter(o -> o.getStatus() == s).count());
        }
        Map<String, Long> porCategoria = contar(todas, o -> o.getCategoria().getNome());
        Map<String, Long> porBairro = contar(todas, o -> o.getBairro() == null ? "Não informado" : o.getBairro());
        long atrasadas = todas.stream().filter(this::estaAtrasada).count();
        OptionalDouble media = todas.stream().filter(o -> o.getAvaliacaoNota() != null)
                .mapToInt(Ocorrencia::getAvaliacaoNota).average();
        Double notaMedia = media.isPresent() ? Math.round(media.getAsDouble() * 10) / 10.0 : null;

        // tempo médio de solução: do registro até a conclusão, em dias com uma casa decimal
        List<Ocorrencia> concluidas = todas.stream()
                .filter(o -> o.getStatus() == CONCLUIDA && o.getDataConclusao() != null).toList();
        OptionalDouble minutos = concluidas.stream()
                .mapToLong(o -> java.time.Duration.between(o.getDataCriacao(), o.getDataConclusao()).toMinutes())
                .average();
        Double tempoMedioSolucaoDias = minutos.isPresent()
                ? Math.round(minutos.getAsDouble() / 60 / 24 * 10) / 10.0 : null;
        long concluidasNoPrazo = concluidas.stream()
                .filter(o -> o.getPrazoPrevisto() == null || !o.getDataConclusao().toLocalDate().isAfter(o.getPrazoPrevisto()))
                .count();
        Integer percentualNoPrazo = concluidas.isEmpty() ? null
                : (int) Math.round(100.0 * concluidasNoPrazo / concluidas.size());

        return new OcorrenciaDto.Indicadores(todas.size(), porStatus.get(CONCLUIDA.name()), atrasadas,
                notaMedia, tempoMedioSolucaoDias, percentualNoPrazo, porStatus, porCategoria, porBairro);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> notificacoesDoCidadao(Long cidadaoId) {
        cidadaos.buscar(cidadaoId);
        return notificacoes.findByCidadaoIdOrderByDataHoraDescIdDesc(cidadaoId);
    }

    @Transactional
    public Notificacao marcarNotificacaoLida(Long notificacaoId) {
        Notificacao n = notificacoes.findById(notificacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", notificacaoId));
        n.setLida(true);
        return n;
    }

    // ---------- apoio interno ----------

    /** Muda o status, grava o histórico (RF06) e notifica autor e apoiadores (RF07). */
    private void registrarMudanca(Ocorrencia o, StatusOcorrencia novo, String observacao) {
        o.setStatus(novo);
        historicos.save(new HistoricoStatus(o, novo, observacao));
        String msg = "Protocolo " + o.getProtocolo() + ": status " + novo + " — " + observacao;
        notificacoes.save(new Notificacao(o.getCidadao(), o, msg));
        o.getApoiadores().forEach(a -> notificacoes.save(new Notificacao(a, o, msg)));
    }

    private void exigirStatus(Ocorrencia o, String acao, StatusOcorrencia... permitidos) {
        if (!Arrays.asList(permitidos).contains(o.getStatus())) {
            throw new RegraNegocioException("Não é possível " + acao + " uma ocorrência com status "
                    + o.getStatus() + " (permitido: " + Arrays.toString(permitidos) + ")");
        }
    }

    private boolean estaAtrasada(Ocorrencia o) {
        if (FINALIZADAS.contains(o.getStatus()) || o.getPrazoPrevisto() == null) {
            return false;
        }
        return LocalDate.now().isAfter(o.getPrazoPrevisto());
    }

    private static Map<String, Long> contar(List<Ocorrencia> lista, Function<Ocorrencia, String> chave) {
        return lista.stream().collect(Collectors.groupingBy(chave, TreeMap::new, Collectors.counting()));
    }

    /** Distância em metros pela fórmula de Haversine. */
    static double distanciaMetros(double lat1, double lng1, double lat2, double lng2) {
        double r = 6_371_000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * r * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
