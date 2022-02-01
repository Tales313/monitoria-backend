package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Vaga;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.web.request.VagaRequest;
import br.com.monitoria.web.response.VagaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EditalRepository editalRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VagaResponse cadastrarVaga(@Valid @RequestBody VagaRequest request) {

        Vaga vaga = request.toModel(editalRepository);
        vaga = vagaRepository.save(vaga);

        return new VagaResponse(vaga);

    }

}
