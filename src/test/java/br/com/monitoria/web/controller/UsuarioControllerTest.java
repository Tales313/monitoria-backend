package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.service.HashService;
import br.com.monitoria.web.request.UsuarioRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HashService hashService;

    private ResultActions enviarPost(UsuarioRequest request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void sucessoAoTentarCriarUsuario() throws Exception {

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370001");

        enviarPost(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.login").value("teste@gmail.com"))
            .andExpect(jsonPath("$.matricula").value("20221370001"));

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");

        assertTrue(usuarioOptional.isPresent());

        Usuario usuario = usuarioOptional.get();

        assertTrue(hashService.isSenhaValida("123456", usuario.getSenha()));
        assertEquals("20221370001", usuario.getMatricula());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComEmailEmBranco() throws Exception {

        UsuarioRequest request = new UsuarioRequest("", "123456", "20221370001");

        enviarPost(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("não deve estar em branco"));

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("");

        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComEmailInvalido() throws Exception {

        UsuarioRequest request = new UsuarioRequest("abc123", "123456", "20221370001");

        enviarPost(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("deve ser um endereço de e-mail bem formado"));

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("abc123");

        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComEmailQueJaExiste() throws Exception {
        Usuario usuario = new Usuario("teste@gmail.com", "123456", "20221370001");
        usuarioRepository.save(usuario);

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370002");

        enviarPost(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Já existe um usuário com este email"));


        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isPresent());
        assertEquals(1L, usuarioRepository.count());
    }

}