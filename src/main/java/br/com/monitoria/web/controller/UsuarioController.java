package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.util.HashService;
import br.com.monitoria.web.request.UsuarioRequest;
import br.com.monitoria.web.response.UsuarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HashService hashService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarUsuario(@RequestBody @Valid UsuarioRequest request) {
        String senha = hashService.hash(request.getSenha());
        Usuario usuario = new Usuario(request.getLogin(), senha, request.getMatricula());
        usuarioRepository.save(usuario);
        return new UsuarioResponse(usuario.getLogin(), usuario.getSenha(), usuario.getDataCadastro(), usuario.getMatricula());
    }

}