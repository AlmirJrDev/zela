package br.unasp.zela.config;

import br.unasp.zela.model.Categoria;
import br.unasp.zela.model.Secretaria;
import br.unasp.zela.repository.CategoriaRepository;
import br.unasp.zela.repository.SecretariaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Cadastra categorias e secretarias básicas quando o banco sobe vazio. */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final SecretariaRepository secretarias;

    public DadosIniciais(CategoriaRepository categorias, SecretariaRepository secretarias) {
        this.categorias = categorias;
        this.secretarias = secretarias;
    }

    @Override
    public void run(String... args) {
        if (categorias.count() == 0) {
            categorias.saveAll(List.of(
                    new Categoria("Buraco na via", 15),
                    new Categoria("Lâmpada queimada", 7),
                    new Categoria("Lixo acumulado", 3),
                    new Categoria("Calçada danificada", 20)));
        }
        if (secretarias.count() == 0) {
            secretarias.saveAll(List.of(
                    new Secretaria("Secretaria de Obras", "obras@prefeitura.exemplo.gov.br"),
                    new Secretaria("Secretaria de Serviços Urbanos", "servicos@prefeitura.exemplo.gov.br")));
        }
    }
}
