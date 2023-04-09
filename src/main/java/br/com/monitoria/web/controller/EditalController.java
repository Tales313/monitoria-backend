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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Paths.EDITAIS)
public class EditalController {

    private EditalRepository editalRepository;

    private Logger log = LoggerFactory.getLogger(EditalController.class);

    public EditalController(
            EditalRepository editalRepository
    ) {
        this.editalRepository = editalRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditalResponse cadastrarEdital(@Valid @RequestBody EditalRequest request) {
        log.info("Inicio de cadastro do edital {}", request);

        if(request.getInicioInscricoes().isAfter(request.getFimInscricoes()))
            throw new DataInscricoesException("edital.data.inicio.invalida");

        // pegando usuario logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Edital edital = request.toModel(usuario);
        edital = editalRepository.save(edital);

        log.info("Edital cadastrado");

        return new EditalResponse(edital);

    }

    @GetMapping("/{id}")
    public EditalResponse buscarEdital(@PathVariable Long id) {
        log.info("Inicio da busca do edital com id: {}", id);

        Edital edital = editalRepository.findById(id).orElseThrow(
                () -> new NotFoundException("edital.nao.encontrado"));

        log.info("Fim da busca de edital");

        return new EditalResponse(edital);

    }

    @GetMapping(Paths.ATIVO)
    public EditalResponse buscarEditalAtivo() {
        log.info("Inicio da busca de edital ativo");

        Edital edital = editalRepository.findTopByOrderByIdDesc().orElseThrow(
                () -> new SemEditalAtivoException("edital.nenhum.cadastrado"));

        log.info("Fim da busca de edital ativo");

        return new EditalResponse(edital);
    }

}
