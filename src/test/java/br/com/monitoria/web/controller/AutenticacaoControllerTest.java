package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Perfil;
import br.com.monitoria.domain.PerfilEnum;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.PerfilRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.web.request.LoginRequest;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private PerfilRepository perfilRepository;

    private ObjectMapper objectMapper;
    
    public AutenticacaoControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    private ResultActions enviarPost(LoginRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional // precisei adicionar pra que as persistencias nao dessem lazy initialization exception
    void cadastrarUsuarioEAutenticarComSucesso() throws Exception {
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();
        Usuario usuario = new Usuario("teste@gmail.com", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011", perfilAluno);
        perfilAluno.addUsuario(usuario);
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