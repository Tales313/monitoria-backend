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

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Vaga vaga = request.toModel(editalRepository, usuario);
        vaga = vagaRepository.save(vaga);

        return new VagaResponse(vaga);

    }

    @GetMapping("/{id_edital}")
    public List<VagaResponse> buscarVagasPorEdital(@PathVariable("id_edital") Long id) {
        editalRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Esse edital não existe"));

        List<Vaga> vagas = vagaRepository.findByEditalId(id);

        return vagas.stream().map(VagaResponse::new).collect(Collectors.toList());
    }

}
