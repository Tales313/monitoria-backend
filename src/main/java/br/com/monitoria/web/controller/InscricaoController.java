package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.util.CalculaMedia;
import br.com.monitoria.web.request.InscricaoRequest;
import br.com.monitoria.web.response.InscricaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    private InscricaoRepository inscricaoRepository;

    private VagaRepository vagaRepository;

    public InscricaoController(
        InscricaoRepository inscricaoRepository,
        VagaRepository vagaRepository
    ) {
        this.inscricaoRepository = inscricaoRepository;
        this.vagaRepository = vagaRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscricaoResponse cadastrarInscricao(@Valid @RequestBody InscricaoRequest request) {

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Double media = CalculaMedia.calcular(request.getNotaDisciplina(), request.getCre());

        Inscricao inscricao = request.toModel(vagaRepository, media, usuario);
        inscricao = inscricaoRepository.save(inscricao);

        return new InscricaoResponse(inscricao);

    }

}
