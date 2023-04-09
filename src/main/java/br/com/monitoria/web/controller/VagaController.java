package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.VagaRequest;
import br.com.monitoria.web.response.VagaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Paths.VAGAS)
public class VagaController {

    private VagaRepository vagaRepository;

    private EditalRepository editalRepository;

    private Logger log = LoggerFactory.getLogger(VagaController.class);

    public VagaController(
        VagaRepository vagaRepository,
        EditalRepository editalRepository
    ) {
        this.vagaRepository = vagaRepository;
        this.editalRepository = editalRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VagaResponse cadastrarVaga(@Valid @RequestBody VagaRequest request) {
        log.info("Inicio do cadastro da vaga: {}", request);

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vaga vaga = request.toModel(editalRepository, usuario);
        vaga = vagaRepository.save(vaga);

        log.info("Vaga cadastrada");

        return new VagaResponse(vaga);

    }

    @GetMapping("/{id_edital}")
    public List<VagaResponse> buscarVagasPorEdital(@PathVariable("id_edital") Long id) {
        log.info("Inicio da busca de vagas do edital: {}", id);

        editalRepository.findById(id).orElseThrow(
                () -> new NotFoundException("edital.nao.encontrado"));

        List<Vaga> vagas = vagaRepository.findByEditalId(id);

        log.info("Fim da busca de vagas");

        return vagas.stream().map(VagaResponse::new).collect(Collectors.toList());
    }

}
