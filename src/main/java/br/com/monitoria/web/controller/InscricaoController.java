package br.com.monitoria.web.controller;

import br.com.monitoria.domain.*;
import br.com.monitoria.exception.NotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger log = LoggerFactory.getLogger(InscricaoController.class);

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
        log.info("Inicio do cadastro da inscricao: {}", request);

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vaga vaga = vagaRepository.findById(request.getIdVaga()).orElseThrow(
                () -> new NotFoundException("inscricao.vaga.nao.existe"));

        inscricaoService.validarInscricaoDoAluno(usuario, vaga, request);

        Double media = CalculaMedia.calcular(request.getNotaDisciplina(), request.getCre());
        Inscricao inscricao = request.toModel(vaga, media, usuario);
        inscricao = inscricaoRepository.save(inscricao);

        log.info("Inscrição cadastrada");

        return new InscricaoResponse(inscricao);

    }

    @GetMapping(Paths.PROXIMA_OPCAO)
    public ProximaOpcaoResponse getProximaOpcao() {
        log.info("Inicio da busca por próxima opção de inscrição");

        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Edital edital = editalRepository.findTopByOrderByIdDesc().orElseThrow(
                () -> new SemEditalAtivoException("edital.nenhum.cadastrado"));

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

        log.info("Fim da busca por próxima opção de inscrição");

        return new ProximaOpcaoResponse(proximaOpcao);
    }

    @GetMapping(Paths.RESULTADOS)
    public List<ResultadoResponse> getResultados(@RequestParam Long editalId) {
        log.info("Inicio da busca de resultados do edital: {}", editalId);

        // pegando resultados ordenados pela disciplina (alfabética), media, cre e nota decrescentes
        List<Inscricao> inscricoes = inscricaoRepository.
                findByVagaEditalIdOrderByVagaDisciplinaAscMediaDescCreDescNotaDisciplinaDesc(editalId);

        log.info("Fim da busca de resultados");

        return inscricoes.stream().map(
                ResultadoResponse::new).collect(Collectors.toList());
    }

}
