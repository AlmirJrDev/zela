package br.unasp.zela.service;

import br.unasp.zela.dto.CategoriaDto;
import br.unasp.zela.exception.RecursoNaoEncontradoException;
import br.unasp.zela.exception.RegraNegocioException;
import br.unasp.zela.model.Categoria;
import br.unasp.zela.repository.CategoriaRepository;
import br.unasp.zela.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repo;
    private final OcorrenciaRepository ocorrencias;

    public CategoriaService(CategoriaRepository repo, OcorrenciaRepository ocorrencias) {
        this.repo = repo;
        this.ocorrencias = ocorrencias;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", id));
    }

    @Transactional
    public Categoria criar(CategoriaDto.Requisicao dto) {
        if (repo.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new RegraNegocioException("Já existe categoria com o nome " + dto.nome());
        }
        return repo.save(new Categoria(dto.nome().trim(), dto.prazoDias()));
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaDto.Requisicao dto) {
        Categoria c = buscar(id);
        c.setNome(dto.nome().trim());
        c.setPrazoDias(dto.prazoDias());
        return c;
    }

    @Transactional
    public void excluir(Long id) {
        Categoria c = buscar(id);
        if (ocorrencias.existsByCategoriaId(id)) {
            throw new RegraNegocioException("Categoria em uso por ocorrências e não pode ser excluída");
        }
        repo.delete(c);
    }
}
