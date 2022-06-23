package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.web.request.EditalRequest;
import br.com.monitoria.web.request.LoginRequest;
import br.com.monitoria.web.request.UsuarioRequest;
import br.com.monitoria.web.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private ObjectMapper objectMapper;
    
    public AutenticacaoControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do EditalRequest. Sem essa linha ele da erro de jackson.
    }

    private ResultActions enviarPost(LoginRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void cadastrarUsuarioEAutenticarComSucesso() throws Exception {
        Usuario usuario = new Usuario("teste@gmail.com", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011");
        usuarioRepository.save(usuario);
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void naoCadastrarUsuarioEAutenticarComFalha() throws Exception {
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest).andExpect(status().isBadRequest());
    }

}