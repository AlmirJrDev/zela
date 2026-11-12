package br.unasp.zela.service;

import br.unasp.zela.dto.CidadaoDto;
import br.unasp.zela.exception.CredenciaisInvalidasException;
import br.unasp.zela.exception.RecursoNaoEncontradoException;
import br.unasp.zela.exception.RegraNegocioException;
import br.unasp.zela.model.Cidadao;
import br.unasp.zela.repository.CidadaoRepository;
import br.unasp.zela.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CidadaoService {

    private final CidadaoRepository repo;
    private final OcorrenciaRepository ocorrencias;
    private final SenhaHasher hasher;

    public CidadaoService(CidadaoRepository repo, OcorrenciaRepository ocorrencias, SenhaHasher hasher) {
        this.repo = repo;
        this.ocorrencias = ocorrencias;
        this.hasher = hasher;
    }

    @Transactional
    public Cidadao cadastrar(CidadaoDto.Cadastro dto) {
        if (repo.existsByEmailIgnoreCase(dto.email())) {
            throw new RegraNegocioException("Já existe cidadão cadastrado com o e-mail " + dto.email());
        }
        Cidadao c = new Cidadao();
        c.setNome(dto.nome().trim());
        c.setEmail(dto.email().trim().toLowerCase());
        c.setTelefone(dto.telefone());
        c.setSenhaHash(hasher.gerar(dto.senha()));
        return repo.save(c);
    }

    /** RF01: aceita e-mail ou telefone como login. */
    @Transactional(readOnly = true)
    public Cidadao autenticar(CidadaoDto.Login dto) {
        String login = dto.login().trim();
        Cidadao c = (login.contains("@") ? repo.findByEmailIgnoreCase(login) : repo.findByTelefone(login))
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!hasher.confere(dto.senha(), c.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return c;
    }

    @Transactional(readOnly = true)
    public List<Cidadao> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Cidadao buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cidadão", id));
    }

    @Transactional
    public Cidadao atualizar(Long id, CidadaoDto.Atualizacao dto) {
        Cidadao c = buscar(id);
        c.setNome(dto.nome().trim());
        c.setTelefone(dto.telefone());
        return c;
    }

    @Transactional
    public void excluir(Long id) {
        Cidadao c = buscar(id);
        if (ocorrencias.existsByCidadaoId(id)) {
            throw new RegraNegocioException("Cidadão possui ocorrências registradas e não pode ser excluído");
        }
        repo.delete(c);
    }
}
