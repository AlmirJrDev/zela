package br.unasp.zela.service;

import br.unasp.zela.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/** Guarda as fotos das ocorrências e das conclusões numa pasta local (RF02, RF12). */
@Service
public class FotoService {

    /** RNF08: fotos de no máximo 1 MB (o app comprime antes de enviar). */
    public static final long TAMANHO_MAXIMO = 1024 * 1024;

    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private static final Pattern NOME_VALIDO = Pattern.compile("^[0-9a-f\\-]{36}\\.(jpg|png|webp)$");

    private final Path pasta;

    public FotoService(@Value("${zela.fotos.pasta:uploads}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.pasta);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar a pasta de fotos " + this.pasta, e);
        }
    }

    /** Salva a foto e devolve o nome gerado. */
    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Envie a foto no campo 'arquivo'");
        }
        String extensao = EXTENSOES.get(arquivo.getContentType());
        if (extensao == null) {
            throw new IllegalArgumentException("Formato não aceito: envie JPEG, PNG ou WEBP");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("A foto deve ter no máximo 1 MB");
        }
        String nome = UUID.randomUUID() + extensao;
        try {
            arquivo.transferTo(pasta.resolve(nome));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar a foto", e);
        }
        return nome;
    }

    public Resource carregar(String nome) {
        if (!NOME_VALIDO.matcher(nome).matches()) {
            throw new RecursoNaoEncontradoException("Foto", nome);
        }
        Path arquivo = pasta.resolve(nome);
        if (!Files.exists(arquivo)) {
            throw new RecursoNaoEncontradoException("Foto", nome);
        }
        return new PathResource(arquivo);
    }

    public String tipoDoArquivo(String nome) {
        if (nome.endsWith(".png")) return "image/png";
        if (nome.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}
