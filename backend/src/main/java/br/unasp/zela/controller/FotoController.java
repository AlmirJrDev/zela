package br.unasp.zela.controller;

import br.unasp.zela.service.FotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

/** Upload e leitura das fotos (RF02: foto da ocorrência; RF12: foto da conclusão). */
@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoService service;

    public FotoController(FotoService service) {
        this.service = service;
    }

    /** Recebe a foto (multipart, campo "arquivo") e devolve a URL para usar em fotoUrl ou fotoConclusaoUrl. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviar(@RequestParam("arquivo") MultipartFile arquivo) {
        String nome = service.salvar(arquivo);
        URI url = ServletUriComponentsBuilder.fromCurrentRequest().path("/{nome}").buildAndExpand(nome).toUri();
        return ResponseEntity.created(url).body(Map.of("nome", nome, "url", url.toString()));
    }

    @GetMapping("/{nome}")
    public ResponseEntity<Resource> ver(@PathVariable String nome) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(service.tipoDoArquivo(nome)))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)))
                .body(service.carregar(nome));
    }
}
