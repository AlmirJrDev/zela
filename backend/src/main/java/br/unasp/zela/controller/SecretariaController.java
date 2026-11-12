package br.unasp.zela.controller;

import br.unasp.zela.dto.SecretariaDto;
import br.unasp.zela.model.Secretaria;
import br.unasp.zela.service.SecretariaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/secretarias")
public class SecretariaController {

    private final SecretariaService service;

    public SecretariaController(SecretariaService service) {
        this.service = service;
    }

    @GetMapping
    public List<SecretariaDto.Resposta> listar() {
        return service.listar().stream().map(SecretariaDto.Resposta::de).toList();
    }

    @GetMapping("/{id}")
    public SecretariaDto.Resposta buscar(@PathVariable Long id) {
        return SecretariaDto.Resposta.de(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<SecretariaDto.Resposta> criar(@Valid @RequestBody SecretariaDto.Requisicao dto) {
        Secretaria s = service.criar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(s.getId()).toUri();
        return ResponseEntity.created(uri).body(SecretariaDto.Resposta.de(s));
    }

    @PutMapping("/{id}")
    public SecretariaDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody SecretariaDto.Requisicao dto) {
        return SecretariaDto.Resposta.de(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
