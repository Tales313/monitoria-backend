package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.exception.DataInscricoesException;
import br.com.monitoria.exception.NotFoundException;
import br.com.monitoria.exception.SemEditalAtivoException;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.EditalRequest;
import br.com.monitoria.web.response.EditalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Paths.EDITAIS)
public class EditalController {

    private EditalRepository editalRepository;

    public EditalController(
            EditalRepository editalRepository
    ) {
        this.editalRepository = editalRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditalResponse cadastrarEdital(@Valid @RequestBody EditalRequest request) {

        if(request.getInicioInscricoes().isAfter(request.getFimInscricoes()))
            throw new DataInscricoesException("edital.data.inicio.invalida");

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Edital edital = request.toModel(usuario);
        edital = editalRepository.save(edital);

        return new EditalResponse(edital);

    }

    @GetMapping("/{id}")
    public EditalResponse buscarEdital(@PathVariable Long id) {

        Edital edital = editalRepository.findById(id).orElseThrow(
                () -> new NotFoundException("edital.nao.encontrado"));

        return new EditalResponse(edital);

    }

    @GetMapping(Paths.ATIVO)
    public EditalResponse buscarEditalAtivo() {
        Edital edital = editalRepository.findTopByOrderByIdDesc().orElseThrow(
                () -> new SemEditalAtivoException("edital.nenhum.cadastrado"));

        return new EditalResponse(edital);
    }

}
