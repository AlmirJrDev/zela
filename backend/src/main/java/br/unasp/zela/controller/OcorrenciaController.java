package br.unasp.zela.controller;

import br.unasp.zela.dto.OcorrenciaDto;
import br.unasp.zela.model.Ocorrencia;
import br.unasp.zela.model.StatusOcorrencia;
import br.unasp.zela.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    // ----- App do cidadão -----

    /** RF02 + RF04 — registra a ocorrência e devolve o protocolo. */
    @PostMapping("/ocorrencias")
    public ResponseEntity<OcorrenciaDto.Resposta> registrar(@Valid @RequestBody OcorrenciaDto.Registro dto) {
        Ocorrencia o = service.registrar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(o.getId()).toUri();
        return ResponseEntity.created(uri).body(OcorrenciaDto.Resposta.de(o));
    }

    @GetMapping("/ocorrencias")
    public List<OcorrenciaDto.Resposta> listar(@RequestParam(required = false) StatusOcorrencia status,
                                               @RequestParam(required = false) Long categoriaId,
                                               @RequestParam(required = false) String bairro) {
        return service.listar(status, categoriaId, bairro).stream().map(OcorrenciaDto.Resposta::de).toList();
    }

    /** RF08 — ocorrências próximas de um ponto. */
    @GetMapping("/ocorrencias/proximas")
    public List<OcorrenciaDto.ProximaResposta> proximas(@RequestParam double lat, @RequestParam double lng,
                                                        @RequestParam(defaultValue = "500") int raioMetros) {
        return service.proximas(lat, lng, raioMetros);
    }

    @GetMapping("/ocorrencias/protocolo/{protocolo}")
    public OcorrenciaDto.Resposta buscarPorProtocolo(@PathVariable String protocolo) {
        return OcorrenciaDto.Resposta.de(service.buscarPorProtocolo(protocolo));
    }

    @GetMapping("/ocorrencias/{id}")
    public OcorrenciaDto.Resposta buscar(@PathVariable Long id) {
        return OcorrenciaDto.Resposta.de(service.buscar(id));
    }

    /** RF06 — histórico de status e prazo previsto. */
    @GetMapping("/ocorrencias/{id}/historico")
    public OcorrenciaDto.Acompanhamento historico(@PathVariable Long id) {
        return service.acompanhar(id);
    }

    @PutMapping("/ocorrencias/{id}")
    public OcorrenciaDto.Resposta editar(@PathVariable Long id, @Valid @RequestBody OcorrenciaDto.Edicao dto) {
        return OcorrenciaDto.Resposta.de(service.editar(id, dto));
    }

    @DeleteMapping("/ocorrencias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    /** RF09 — apoiar uma ocorrência existente. */
    @PostMapping("/ocorrencias/{id}/apoios")
    public OcorrenciaDto.Resposta apoiar(@PathVariable Long id, @Valid @RequestBody OcorrenciaDto.Apoio dto) {
        return OcorrenciaDto.Resposta.de(service.apoiar(id, dto.cidadaoId()));
    }

    /** RF13 — avaliar o atendimento. */
    @PostMapping("/ocorrencias/{id}/avaliacao")
    public OcorrenciaDto.Resposta avaliar(@PathVariable Long id, @Valid @RequestBody OcorrenciaDto.Avaliacao dto) {
        return OcorrenciaDto.Resposta.de(service.avaliar(id, dto));
    }

    // ----- Painel da prefeitura -----

    /** RF10 — triagem. */
    @PatchMapping("/ocorrencias/{id}/triagem")
    public OcorrenciaDto.Resposta triar(@PathVariable Long id, @Valid @RequestBody OcorrenciaDto.Triagem dto) {
        return OcorrenciaDto.Resposta.de(service.triar(id, dto));
    }

    /** RF11 — encaminhamento. */
    @PatchMapping("/ocorrencias/{id}/encaminhamento")
    public OcorrenciaDto.Resposta encaminhar(@PathVariable Long id,
                                             @Valid @RequestBody OcorrenciaDto.Encaminhamento dto) {
        return OcorrenciaDto.Resposta.de(service.encaminhar(id, dto));
    }

    @PatchMapping("/ocorrencias/{id}/execucao")
    public OcorrenciaDto.Resposta iniciarExecucao(@PathVariable Long id,
                                                  @Valid @RequestBody(required = false) OcorrenciaDto.InicioExecucao dto) {
        return OcorrenciaDto.Resposta.de(service.iniciarExecucao(id, dto));
    }

    /** RF12 — conclusão com foto. */
    @PatchMapping("/ocorrencias/{id}/conclusao")
    public OcorrenciaDto.Resposta concluir(@PathVariable Long id, @Valid @RequestBody OcorrenciaDto.Conclusao dto) {
        return OcorrenciaDto.Resposta.de(service.concluir(id, dto));
    }

    /** RF14 — indicadores do gestor. */
    @GetMapping("/indicadores")
    public OcorrenciaDto.Indicadores indicadores() {
        return service.indicadores();
    }
}
