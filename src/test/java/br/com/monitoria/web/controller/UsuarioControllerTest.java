package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.util.HashService;
import br.com.monitoria.web.request.UsuarioRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    void sucessoEmCriarUsuario() throws Exception {

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370001");

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.login").value("teste@gmail.com"))
            .andExpect(jsonPath("$.matricula").value("20221370001"));

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");

        assertTrue(usuarioOptional.isPresent());

        Usuario usuario = usuarioOptional.get();

        assertTrue(hashService.isSenhaValida("123456", usuario.getSenha()));
        assertEquals("20221370001", usuario.getMatricula());
    }

}