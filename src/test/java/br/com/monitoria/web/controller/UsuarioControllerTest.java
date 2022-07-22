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

    public UsuarioControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do UsuarioRequest. Sem essa linha ele da erro de jackson.
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
                .andExpect(jsonPath("$.status").value(status.value()))
                .andExpect(jsonPath("$.error").value(status.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
    }

    @Test
    void sucessoAoTentarCriarUsuario() throws Exception {

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370001", LocalDate.of(1998, 11, 10));

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
        assertEquals(usuario.getPerfilUnico().getNome(), PerfilEnum.ALUNO);
    }

    @Test
    void badRequestAoTentarCriarUsuarioComEmailEmBranco() throws Exception {
        UsuarioRequest request = new UsuarioRequest("", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "O login não deve estar em branco", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComEmailInvalido() throws Exception {
        UsuarioRequest request = new UsuarioRequest("abc123", "123456", "20221370001", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "deve ser um endereço de e-mail bem formado", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("abc123");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    @Transactional // precisei adicionar pra que as persistencias nao dessem lazy initialization exception
    void badRequestAoTentarCriarUsuarioComEmailQueJaExiste() throws Exception {
        Perfil perfilAluno = perfilRepository.findByNome(PerfilEnum.ALUNO).get();
        Usuario usuario = new Usuario("teste@gmail.com", "123456", "20221370001", LocalDate.of(1998, 10, 11), perfilAluno);
        perfilAluno.addUsuario(usuario);
        usuarioRepository.save(usuario);

        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "Já existe um usuário com este email", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isPresent());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComSenhaNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", null, "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "A senha não deve ser nula", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }


    @Test
    void badRequestAoTentarCriarUsuarioComSenhaMenorQue6Digitos() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "12345", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "tamanho deve ser entre 6 e 20", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComSenhaMaiorQue20Digitos() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "abcdefghijklmnopqrstu", "20221370002", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "tamanho deve ser entre 6 e 20", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }


    @Test
    void badRequestAoTentarCriarUsuarioComMatriculaNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", null, LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "A matricula não deve estar em branco", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComMatriculaEmBranco() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "", LocalDate.of(1998, 11, 10));

        enviarPostEValidarRespostaDeErro(request, "A matricula não deve estar em branco", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComDataNascimentoNula() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370002", null);

        enviarPostEValidarRespostaDeErro(request, "A data de nascimento deve ser informada", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

    @Test
    void badRequestAoTentarCriarUsuarioComDataNascimentoNoFuturo() throws Exception {
        UsuarioRequest request = new UsuarioRequest("teste@gmail.com", "123456", "20221370002", LocalDate.now().plusDays(1));

        enviarPostEValidarRespostaDeErro(request, "A data de nascimento não pode ser no futuro", HttpStatus.BAD_REQUEST);

        Optional<Usuario> usuarioOptional = usuarioRepository.findByLogin("teste@gmail.com");
        assertTrue(usuarioOptional.isEmpty());
    }

}