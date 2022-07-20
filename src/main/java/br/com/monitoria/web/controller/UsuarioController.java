package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Perfil;
import br.com.monitoria.domain.PerfilEnum;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.PerfilRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.service.HashService;
import br.com.monitoria.web.request.UsuarioRequest;
import br.com.monitoria.web.response.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private UsuarioRepository usuarioRepository;

    private PerfilRepository perfilRepository;

    private HashService hashService;

    public UsuarioController(
        UsuarioRepository usuarioRepository,
        PerfilRepository perfilRepository,
        HashService hashService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.hashService = hashService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrarUsuario(@RequestBody @Valid UsuarioRequest request) {
        String senha = hashService.hash(request.getSenha());
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();
        Usuario usuario = new Usuario(request.getLogin(), senha, request.getMatricula(), perfilAluno);
        perfilAluno.addUsuario(usuario);
        // É necessario setar o relacionamento dos dois lados, isso garante que a tabela
        // intermediaria do ManyToMany seja alimentada
        usuarioRepository.save(usuario);
        return new UsuarioResponse(usuario.getLogin(), usuario.getMatricula());
    }

}