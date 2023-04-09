package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Perfil;
import br.com.monitoria.domain.PerfilEnum;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.PerfilRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.service.HashService;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.UsuarioRequest;
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
    private PerfilRepository perfilRepository;

    @Autowired
    private HashService hashService;

    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    public UsuarioControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do UsuarioRequest. Sem essa linha ele da erro de jackson.
    }

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    private ResultActions enviarPost(UsuarioRequest request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(Paths.USUARIOS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarRespostaDeErro(UsuarioRequest request, String mensagemDeErro, HttpStatus status) throws Exception {
        enviarPost(request)
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(status.value()))
                .andExpect(jsonPath("$.error").value(status.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.USUARIOS));
    }

    @Test
    void sucessoAoCriarUsuario() throws Exception {

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPost(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.login").value("teste@gmail.com"))
            .andExpect(jsonPath("$.matricula").value("20221370001"));

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");

        assertTrue(usuarioOptional.isPresent());

        Usuario usuario = usuarioOptional.get();

        assertTrue(hashService.isSenhaValida("123456", usuario.getSenha()));
        assertEquals("20221370001", usuario.getMatricula());
        assertNotNull(usuario.getAuthorities());
        assertFalse(usuario.getAuthorities().isEmpty());
        assertEquals(PerfilEnum.ALUNO, usuario.getPerfilUnico().getNome());
    }

    @Test
    void badRequestAoCriarUsuarioComEmailEmBranco() throws Exception {
        UsuarioRequest request = new UsuarioRequest("", "NomeTeste", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.login.branco"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComEmailInvalido() throws Exception {
        UsuarioRequest request = new UsuarioRequest("abc123", "NomeTeste", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.email.invalido"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("abc123");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    @Transactional // precisei adicionar pra que as persistencias nao dessem lazy initialization exception
    void badRequestAoCriarUsuarioComEmailQueJaExiste() throws Exception {
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();
        Usuario usuario = new Usuario("teste@gmail.com", "NomeTeste", "123456", "20221370001", LocalDate.of(1998, 10, 11), perfilAluno);
        perfilAluno.addUsuario(usuario);
        usuarioRepository.save(usuario);

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.email.ja.existe"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isPresent());
    }

    @Test
    void badRequestAoCriarUsuarioComNomeEmBranco() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.nome.branco"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComSenhaNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", null, "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.senha.nao.informada"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComSenhaMenorQue6Digitos() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "12345", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.senha.tamanho"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComSenhaMaiorQue20Digitos() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "abcdefghijklmnopqrstu", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.senha.tamanho"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComMatriculaNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", null, LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.matricula.branco"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComMatriculaEmBranco() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", "", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.matricula.branco"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComDataNascimentoNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", "20221370002", null);

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.dataNascimento.nao.informada"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoCriarUsuarioComDataNascimentoNoFuturo() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "NomeTeste", "123456", "20221370002", LocalDate.now().plusDays(1));

        enviarPostEValidarRespostaDeErro(request, getMessageSource("usuario.dataNascimento.futuro"), HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

}