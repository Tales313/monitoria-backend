package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.EditalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.monitoria.testUtils.UsuarioTestUtils.autenticarComAluno;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static br.com.monitoria.testUtils.UsuarioTestUtils.autenticarComAdmin;


@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EditalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private ObjectMapper objectMapper;

    private String token;

    private Usuario usuario;

    @Autowired
    private MessageSource messageSource;

    public EditalControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do EditalRequest. Sem essa linha ele da erro de jackson.
    }

    @BeforeEach
    void setUp() throws Exception {
        this.token = autenticarComAdmin(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("admin@gmail.com").get();
    }

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    private ResultActions enviarPost(EditalRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post(Paths.EDITAIS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarRespostaDeErro(EditalRequest request, String mensagemDeErro, HttpStatus httpStatus) throws Exception {
        enviarPost(request)
                .andExpect(status().is(httpStatus.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(httpStatus.value()))
                .andExpect(jsonPath("$.error").value(httpStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.EDITAIS));
    }

    private ResultActions enviarGetById(Long id) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get(Paths.EDITAIS + "/" + id)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions enviarGetEditalAtivo() throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get(Paths.EDITAIS + Paths.ATIVO)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void sucessoAoCriarEdital() throws Exception {

        EditalRequest editalRequest = new EditalRequest("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPost(editalRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.semestre").value("2022.2"))
                .andExpect(jsonPath("$.inicioInscricoes").value("2022-07-01"))
                .andExpect(jsonPath("$.fimInscricoes").value("2022-07-15"));

        Optional<Edital> editalOptional = editalRepository.findBySemestre("2022.2");

        assertTrue(editalOptional.isPresent());

        Edital edital = editalOptional.get();

        assertEquals(1L, edital.getId());
        assertEquals("2022.2", edital.getSemestre());
        assertEquals("2022-07-01", edital.getInicioInscricoes().toString());
        assertEquals("2022-07-15", edital.getFimInscricoes().toString());
        assertEquals(1L, edital.getUsuario().getId());
    }

    @Test
    void badRequestAoCriarEditalComSemestreNulo() throws Exception {
        EditalRequest editalRequest = new EditalRequest(null, LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.semestre.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void badRequestAoCriarEditalComSemestreEmBranco() throws Exception {
        EditalRequest editalRequest = new EditalRequest("", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.semestre.formato"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void badRequestAoCriarEditalComSemestreInvalido() throws Exception {
        EditalRequest editalRequest = new EditalRequest("123.4", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.semestre.formato"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void badRequestAoCriarEditalComInicioInscricoesNula() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", null, LocalDate.of(2022, 7, 15));

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.data.inicio.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void badRequestAoCriarEditalComFimInscricoesNula() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", LocalDate.of(2022, 7, 1), null);

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.data.fim.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void badRequestAoCriarEditalComDataInicioInscricoesAposDataFimInscricoes() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", LocalDate.of(2022, 7, 15), LocalDate.of(2022, 7, 1));

        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("edital.data.inicio.invalida"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void forbiddenAoCriarEditalComTokenDeUsuarioAluno() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);

        EditalRequest editalRequest = new EditalRequest("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));
        enviarPostEValidarRespostaDeErro(editalRequest, getMessageSource("usuario.sem.autorizacao"), HttpStatus.FORBIDDEN);

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void sucessoAoBuscarEdital() throws Exception {
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        edital = editalRepository.save(edital);

        enviarGetById(edital.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(edital.getId()))
                .andExpect(jsonPath("$.semestre").value(edital.getSemestre()))
                .andExpect(jsonPath("$.inicioInscricoes").value(edital.getInicioInscricoes().toString()))
                .andExpect(jsonPath("$.fimInscricoes").value(edital.getFimInscricoes().toString()));
    }

    @Test
    void badRequestAoBuscarEditalNaoExistente() throws Exception {

        enviarGetById(1L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(getMessageSource("edital.nao.encontrado")))
                .andExpect(jsonPath("$.path").value(Paths.EDITAIS + "/1"));
    }

    @Test
    void sucessoAoBuscarPeloEditalAtivo() throws Exception {
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        edital = editalRepository.save(edital);

        enviarGetEditalAtivo()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(edital.getId()))
                .andExpect(jsonPath("$.semestre").value(edital.getSemestre()))
                .andExpect(jsonPath("$.inicioInscricoes").value(edital.getInicioInscricoes().toString()))
                .andExpect(jsonPath("$.fimInscricoes").value(edital.getFimInscricoes().toString()));
    }

    @Test
    void unprocessableEntityAoBuscarPeloEditalAtivoEEleNaoExiste() throws Exception {
        enviarGetEditalAtivo()
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(getMessageSource("edital.nenhum.cadastrado")))
                .andExpect(jsonPath("$.path").value(Paths.EDITAIS + Paths.ATIVO));
    }

}