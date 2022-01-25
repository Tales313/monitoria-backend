package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.web.request.EditalRequest;
import br.com.monitoria.web.response.EditalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/editais")
public class EditalController {

    @Autowired
    private EditalRepository editalRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditalResponse cadastrarEdital(@Valid @RequestBody EditalRequest request) {

        Edital edital = request.toModel();
        edital = editalRepository.save(edital);

        return new EditalResponse(edital);

    }

    @GetMapping("/{id}")
    public EditalResponse buscarEdital(@PathVariable Long id) {

        Edital edital = editalRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Edital não encontrado"));

        return new EditalResponse(edital);

    }

}
