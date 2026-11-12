package br.unasp.zela.controller;

import br.unasp.zela.dto.CategoriaDto;
import br.unasp.zela.model.Categoria;
import br.unasp.zela.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaDto.Resposta> listar() {
        return service.listar().stream().map(CategoriaDto.Resposta::de).toList();
    }

    @GetMapping("/{id}")
    public CategoriaDto.Resposta buscar(@PathVariable Long id) {
        return CategoriaDto.Resposta.de(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaDto.Resposta> criar(@Valid @RequestBody CategoriaDto.Requisicao dto) {
        Categoria c = service.criar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(c.getId()).toUri();
        return ResponseEntity.created(uri).body(CategoriaDto.Resposta.de(c));
    }

    @PutMapping("/{id}")
    public CategoriaDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDto.Requisicao dto) {
        return CategoriaDto.Resposta.de(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
