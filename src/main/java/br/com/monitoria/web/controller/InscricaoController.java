package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.util.CalculaMedia;
import br.com.monitoria.util.SQSService;
import br.com.monitoria.web.request.InscricaoRequest;
import br.com.monitoria.web.response.InscricaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    public InscricaoController(
            @Autowired InscricaoRepository inscricaoRepository,
            @Autowired VagaRepository vagaRepository,
            @Autowired SQSService sqsService
    ) {
        this.inscricaoRepository = inscricaoRepository;
        this.vagaRepository = vagaRepository;
        this.sqsService = sqsService;
    }

    private InscricaoRepository inscricaoRepository;
    private VagaRepository vagaRepository;
    private SQSService sqsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse cadastrarInscricao(@Valid @RequestBody InscricaoRequest request) {

        Double media = CalculaMedia.calcular(request.getNotaDisciplina(), request.getCre());

        Inscricao inscricao = request.toModel(vagaRepository, media);
        inscricao = inscricaoRepository.save(inscricao);

//        sqsService.enviarEmailDeInscricao(inscricao.getVaga().getDisciplina());

        return new InscricaoResponse(inscricao);

    }

}
