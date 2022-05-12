package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Inscricao;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.util.CalculaMedia;
import br.com.monitoria.web.request.InscricaoRequest;
import br.com.monitoria.web.response.InscricaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private VagaRepository vagaRepository;

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
