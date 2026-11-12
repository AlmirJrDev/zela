package br.unasp.zela.controller;

import br.unasp.zela.dto.CidadaoDto;
import br.unasp.zela.dto.OcorrenciaDto;
import br.unasp.zela.model.Cidadao;
import br.unasp.zela.service.CidadaoService;
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
public class CidadaoController {

    private final CidadaoService service;
    private final OcorrenciaService ocorrencias;

    public CidadaoController(CidadaoService service, OcorrenciaService ocorrencias) {
        this.service = service;
        this.ocorrencias = ocorrencias;
    }

    /** RF01 — cadastro. */
    @PostMapping("/cidadaos")
    public ResponseEntity<CidadaoDto.Resposta> cadastrar(@Valid @RequestBody CidadaoDto.Cadastro dto) {
        Cidadao c = service.cadastrar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(c.getId()).toUri();
        return ResponseEntity.created(uri).body(CidadaoDto.Resposta.de(c));
    }

    /** RF01 — autenticação por e-mail ou telefone. */
    @PostMapping("/auth/login")
    public CidadaoDto.LoginResposta login(@Valid @RequestBody CidadaoDto.Login dto) {
        Cidadao c = service.autenticar(dto);
        return new CidadaoDto.LoginResposta("Login realizado com sucesso", CidadaoDto.Resposta.de(c));
    }

    @GetMapping("/cidadaos")
    public List<CidadaoDto.Resposta> listar() {
        return service.listar().stream().map(CidadaoDto.Resposta::de).toList();
    }

    @GetMapping("/cidadaos/{id}")
    public CidadaoDto.Resposta buscar(@PathVariable Long id) {
        return CidadaoDto.Resposta.de(service.buscar(id));
    }

    @PutMapping("/cidadaos/{id}")
    public CidadaoDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody CidadaoDto.Atualizacao dto) {
        return CidadaoDto.Resposta.de(service.atualizar(id, dto));
    }

    @DeleteMapping("/cidadaos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    /** RF05 — minhas ocorrências com o status atual. */
    @GetMapping("/cidadaos/{id}/ocorrencias")
    public List<OcorrenciaDto.Resposta> ocorrenciasDoCidadao(@PathVariable Long id) {
        return ocorrencias.listarDoCidadao(id).stream().map(OcorrenciaDto.Resposta::de).toList();
    }

    /** RF07 — notificações recebidas a cada mudança de status. */
    @GetMapping("/cidadaos/{id}/notificacoes")
    public List<OcorrenciaDto.NotificacaoResposta> notificacoes(@PathVariable Long id) {
        return ocorrencias.notificacoesDoCidadao(id).stream().map(OcorrenciaDto.NotificacaoResposta::de).toList();
    }

    @PatchMapping("/notificacoes/{id}/lida")
    public OcorrenciaDto.NotificacaoResposta marcarLida(@PathVariable Long id) {
        return OcorrenciaDto.NotificacaoResposta.de(ocorrencias.marcarNotificacaoLida(id));
    }
}
