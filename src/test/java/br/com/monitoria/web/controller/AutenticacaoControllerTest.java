package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Perfil;
import br.com.monitoria.domain.PerfilEnum;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.PerfilRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

    @Autowired
    private MessageSource messageSource;

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }
    public AutenticacaoControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    private ResultActions enviarPost(LoginRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post(Paths.AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional // precisei adicionar pra que as persistencias nao dessem lazy initialization exception
    void sucessoAoAutenticarAluno() throws Exception {
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();
        Usuario usuario = new Usuario("teste@gmail.com", "NomeTeste", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011", LocalDate.of(1998, 10, 11), perfilAluno);
        perfilAluno.addUsuario(usuario);
        usuarioRepository.save(usuario);
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.perfil").value(PerfilEnum.ALUNO.toString()));
    }

    @Test
    @Transactional
    void sucessoAoAutenticarCoordenador() throws Exception {
        Perfil perfilCoordenador = perfilRepository.findByNome(PerfilEnum.COORDENADOR).get();
        Usuario usuario = new Usuario("teste@gmail.com", "NomeTeste", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011", LocalDate.of(1998, 10, 11), perfilCoordenador);
        perfilCoordenador.addUsuario(usuario);
        usuarioRepository.save(usuario);
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.perfil").value(PerfilEnum.COORDENADOR.toString()));
    }

    @Test
    @Transactional
    void sucessoAoAutenticarAdmin() throws Exception {
        Perfil perfilAdmin = perfilRepository.findByNome(PerfilEnum.ADMIN).get();
        Usuario usuario = new Usuario("teste@gmail.com", "NomeTeste", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011", LocalDate.of(1998, 10, 11), perfilAdmin);
        perfilAdmin.addUsuario(usuario);
        usuarioRepository.save(usuario);
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.perfil").value(PerfilEnum.ADMIN.toString()));
    }

    @Test
    void unauthorizedAoAutenticarComUsuarioInexistente() throws Exception {
        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");

        enviarPost(loginRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(getMessageSource("usuario.login.inexistente")))
                .andExpect(jsonPath("$.path").value(Paths.AUTH));
    }

    @Test
    void unauthorizedAoAutenticarComSenhaErrada() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@gmail.com", "12345");

        enviarPost(loginRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(getMessageSource("usuario.senha.incorreta")))
                .andExpect(jsonPath("$.path").value(Paths.AUTH));
    }

}