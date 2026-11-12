package br.unasp.zela.service;

import br.unasp.zela.dto.SecretariaDto;
import br.unasp.zela.exception.RecursoNaoEncontradoException;
import br.unasp.zela.exception.RegraNegocioException;
import br.unasp.zela.model.Secretaria;
import br.unasp.zela.repository.OcorrenciaRepository;
import br.unasp.zela.repository.SecretariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SecretariaService {

    private final SecretariaRepository repo;
    private final OcorrenciaRepository ocorrencias;

    public SecretariaService(SecretariaRepository repo, OcorrenciaRepository ocorrencias) {
        this.repo = repo;
        this.ocorrencias = ocorrencias;
    }

    @Transactional(readOnly = true)
    public List<Secretaria> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Secretaria buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Secretaria", id));
    }

    @Transactional
    public Secretaria criar(SecretariaDto.Requisicao dto) {
        if (repo.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new RegraNegocioException("Já existe secretaria com o nome " + dto.nome());
        }
        return repo.save(new Secretaria(dto.nome().trim(), dto.emailContato()));
    }

    @Transactional
    public Secretaria atualizar(Long id, SecretariaDto.Requisicao dto) {
        Secretaria s = buscar(id);
        s.setNome(dto.nome().trim());
        s.setEmailContato(dto.emailContato());
        return s;
    }

    @Transactional
    public void excluir(Long id) {
        Secretaria s = buscar(id);
        if (ocorrencias.existsBySecretariaId(id)) {
            throw new RegraNegocioException("Secretaria possui ocorrências encaminhadas e não pode ser excluída");
        }
        repo.delete(s);
    }
}
