package br.unasp.zela.service;

import br.unasp.zela.dto.CategoriaDto;
import br.unasp.zela.dto.OcorrenciaDto;
import br.unasp.zela.model.Categoria;
import br.unasp.zela.model.Cidadao;
import br.unasp.zela.model.Ocorrencia;
import br.unasp.zela.model.StatusOcorrencia;
import br.unasp.zela.repository.HistoricoStatusRepository;
import br.unasp.zela.repository.NotificacaoRepository;
import br.unasp.zela.repository.OcorrenciaRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes da RN-01 · Prazo e atraso da ocorrência (RF06) — atividade prática de Testes.
 * Unitários: os repositórios são substituídos por mocks (Mockito), sem banco e sem subir o Spring.
 */
class PrazoOcorrenciaTest {

    private OcorrenciaRepository repo;
    private HistoricoStatusRepository historicos;
    private CidadaoService cidadaos;
    private CategoriaService categorias;
    private OcorrenciaService service;

    private static final LocalDate HOJE = LocalDate.now();

    @BeforeEach
    void preparar() {
        repo = mock(OcorrenciaRepository.class);
        historicos = mock(HistoricoStatusRepository.class);
        cidadaos = mock(CidadaoService.class);
        categorias = mock(CategoriaService.class);
        service = new OcorrenciaService(repo, historicos, mock(NotificacaoRepository.class),
                cidadaos, categorias, mock(SecretariaService.class));
        when(historicos.findByOcorrenciaIdOrderByDataHoraAscIdAsc(anyLong())).thenReturn(List.of());
    }

    /** Cria uma ocorrência já salva, com o status e o prazo informados, e a devolve pelo repositório. */
    private void ocorrenciaSalva(long id, StatusOcorrencia status, LocalDate prazo) {
        Ocorrencia o = new Ocorrencia();
        o.setId(id);
        o.setProtocolo("ZELA-2026-" + String.format("%06d", id));
        o.setStatus(status);
        o.setPrazoPrevisto(prazo);
        when(repo.findById(id)).thenReturn(Optional.of(o));
    }

    // CT-06 · valor-limite −1 | 0 (dias até o prazo): prazo = ontem
    @Test
    @DisplayName("CT-06 · prazo venceu ontem → atrasada")
    void ocorrenciaComPrazoVencidoOntemEstaAtrasada() {
        // Arrange
        ocorrenciaSalva(1L, StatusOcorrencia.ENCAMINHADA, HOJE.minusDays(1));
        // Act
        OcorrenciaDto.Acompanhamento a = service.acompanhar(1L);
        // Assert
        assertTrue(a.atrasada());
    }

    // CT-07 · valor-limite −1 | 0 (dias até o prazo): prazo = hoje
    @Test
    @DisplayName("CT-07 · prazo vence hoje → ainda em dia")
    void ocorrenciaNoDiaDoPrazoAindaNaoEstaAtrasada() {
        // Arrange
        ocorrenciaSalva(2L, StatusOcorrencia.EM_EXECUCAO, HOJE);
        // Act
        OcorrenciaDto.Acompanhamento a = service.acompanhar(2L);
        // Assert
        assertFalse(a.atrasada());
    }

    // CT-12 · tabela de decisão R4 / caminho 1: finalizada com prazo vencido
    @Test
    @DisplayName("CT-12 · concluída com prazo vencido → não conta como atrasada")
    void ocorrenciaConcluidaComPrazoVencidoNaoContaComoAtrasada() {
        // Arrange
        ocorrenciaSalva(3L, StatusOcorrencia.CONCLUIDA, HOJE.minusDays(10));
        // Act
        OcorrenciaDto.Acompanhamento a = service.acompanhar(3L);
        // Assert
        assertFalse(a.atrasada());
    }

    // CT-13 · caminho 2: ocorrência em atendimento sem prazo definido
    @Test
    @DisplayName("CT-13 · sem prazo previsto → não atrasada")
    void ocorrenciaSemPrazoNaoEstaAtrasada() {
        // Arrange
        ocorrenciaSalva(4L, StatusOcorrencia.ABERTA, null);
        // Act
        OcorrenciaDto.Acompanhamento a = service.acompanhar(4L);
        // Assert
        assertFalse(a.atrasada());
    }

    // CT-05 · valor-limite 0 | 1 no cálculo: prazoDias = 1 → prazo é amanhã
    @Test
    @DisplayName("CT-05 · categoria com prazo de 1 dia → prazo previsto é amanhã")
    void registroEmCategoriaDeUmDiaTemPrazoParaAmanha() {
        // Arrange
        Cidadao autor = new Cidadao();
        autor.setId(10L);
        when(cidadaos.buscar(10L)).thenReturn(autor);
        when(categorias.buscar(20L)).thenReturn(new Categoria("Iluminação", 1));
        when(repo.save(any(Ocorrencia.class))).thenAnswer(inv -> {
            Ocorrencia o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });
        OcorrenciaDto.Registro dto = new OcorrenciaDto.Registro(10L, 20L, "Poste apagado", null,
                -22.9, -47.06, "Rua A, 100", "Centro");
        // Act
        Ocorrencia o = service.registrar(dto);
        // Assert
        assertEquals(HOJE.plusDays(1), o.getPrazoPrevisto());
        assertEquals(StatusOcorrencia.ABERTA, o.getStatus());
    }

    // CT-01 a CT-04 · valores-limite do prazoDias da categoria (1 a 365)
    @ParameterizedTest(name = "prazoDias = {0} → válido? {1}")
    @CsvSource({"0, false", "1, true", "365, true", "366, false"})
    @DisplayName("CT-01 a CT-04 · prazo da categoria aceito só de 1 a 365 dias")
    void prazoDaCategoriaSoEhAceitoEntre1e365Dias(int prazoDias, boolean esperadoValido) {
        // Arrange
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CategoriaDto.Requisicao req = new CategoriaDto.Requisicao("Buraco na via", prazoDias);
        // Act
        boolean valido = validator.validate(req).isEmpty();
        // Assert
        assertEquals(esperadoValido, valido);
    }
}
