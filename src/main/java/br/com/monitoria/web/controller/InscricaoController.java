package br.com.monitoria.web.controller;

import br.com.monitoria.domain.*;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.exception.OperacaoNegadaException;
import br.com.monitoria.exception.SemEditalAtivoException;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.service.InscricaoService;
import br.com.monitoria.util.CalculaMedia;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.InscricaoRequest;
import br.com.monitoria.web.response.InscricaoResponse;
import br.com.monitoria.web.response.ProximaOpcaoResponse;
import br.com.monitoria.web.response.ResultadoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Paths.INSCRICOES)
public class InscricaoController {

    private InscricaoRepository inscricaoRepository;

    private VagaRepository vagaRepository;
    
    private EditalRepository editalRepository;

    private InscricaoService inscricaoService;

    public InscricaoController(
        InscricaoRepository inscricaoRepository,
        VagaRepository vagaRepository,
        EditalRepository editalRepository,
        InscricaoService inscricaoService
    ) {
        this.inscricaoRepository = inscricaoRepository;
        this.vagaRepository = vagaRepository;
        this.editalRepository = editalRepository;
        this.inscricaoService = inscricaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse cadastrarInscricao(@Valid @RequestBody InscricaoRequest request) {

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!usuario.getPerfilUnico().getNome().equals(PerfilEnum.ALUNO))
            throw new OperacaoNegadaException("Apenas alunos podem se inscrever para concorrer a monitoria.");

        Vaga vaga = vagaRepository.findById(request.getIdVaga()).orElseThrow(
                () -> new NotFoundException("Vaga não encontrada"));

        inscricaoService.validarInscricaoDoAluno(usuario, vaga, request);

        Double media = CalculaMedia.calcular(request.getNotaDisciplina(), request.getCre());
        Inscricao inscricao = request.toModel(vaga, media, usuario);
        inscricao = inscricaoRepository.save(inscricao);

        return new InscricaoResponse(inscricao);

    }

    @GetMapping(Paths.PROXIMA_OPCAO)
    public ProximaOpcaoResponse getProximaOpcao() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Edital edital = editalRepository.findTopByOrderByIdDesc().orElseThrow(
                () -> new SemEditalAtivoException("Não há nenhum edital cadastrado"));

        int qtdInscricoes = inscricaoRepository.
                findByUsuarioIdAndVagaEditalId(usuario.getId(), edital.getId())
                .size();

        int proximaOpcao = -1;

        switch (qtdInscricoes) {
            case 0:
                proximaOpcao = 1;
                break;
            case 1:
                proximaOpcao = 2;
                break;
        }

        return new ProximaOpcaoResponse(proximaOpcao);
    }

    @GetMapping(Paths.RESULTADOS)
    public List<ResultadoResponse> getResultados(@RequestParam Long editalId) {
        List<Inscricao> inscricoes = inscricaoRepository.findByVagaEditalId(editalId);

        return inscricoes.stream().map(
                ResultadoResponse::new).collect(Collectors.toList());
    }

}
