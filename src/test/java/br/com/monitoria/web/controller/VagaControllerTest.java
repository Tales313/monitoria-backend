package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.VagaRequest;
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

import static br.com.monitoria.testUtils.UsuarioTestUtils.autenticarComAdmin;
import static br.com.monitoria.testUtils.UsuarioTestUtils.autenticarComAluno;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class VagaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private ObjectMapper objectMapper;

    private String token;

    private Usuario usuario;

    private Edital edital;

    @Autowired
    private MessageSource messageSource;

    public VagaControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() throws Exception {
        this.token = autenticarComAdmin(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("admin@gmail.com").get();
        this.edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);
    }

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    private ResultActions enviarPost(VagaRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post(Paths.VAGAS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarRespostaDeErro(VagaRequest request, String mensagemDeErro, HttpStatus status) throws Exception {
        enviarPost(request)
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(status.value()))
                .andExpect(jsonPath("$.error").value(status.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.VAGAS));
    }

    private ResultActions enviarGetPorEditalId(Long idEdital) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get(Paths.VAGAS + "/" + idEdital)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions enviarGetPorEditalIdEValidarRespostaDeErro(Long idEdital, String mensagemDeErro, HttpStatus status) throws Exception {
        return enviarGetPorEditalId(idEdital)
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(status.value()))
                .andExpect(jsonPath("$.error").value(status.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.VAGAS + "/" + idEdital));
    }

    @Test
    void sucessoAoCriarVaga() throws Exception {

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, edital.getId());

        enviarPost(vagaRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.disciplina").value("Javascript"))
                .andExpect(jsonPath("$.periodo").value("2"))
                .andExpect(jsonPath("$.quantidade").value(2));

        Optional<Vaga> optionalVaga = vagaRepository.findById(1L);

        assertTrue(optionalVaga.isPresent());

        Vaga vaga = optionalVaga.get();

        assertEquals(1L, vaga.getId());
        assertEquals("Javascript", vaga.getDisciplina());
        assertEquals("2", vaga.getPeriodo());
        assertEquals(2, vaga.getQuantidade());
        assertEquals(1L, vaga.getEdital().getId());
        assertEquals(1L, vaga.getUsuario().getId());
    }

    @Test
    void badRequestAoCriarVagaComDisciplinaEmBranco() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("", "2", 2, edital.getId());

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("vaga.disciplina.nao.informada"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void badRequestAoCriarVagaComPeriodoEmBranco() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "", 2, edital.getId());

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("vaga.periodo.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void badRequestAoCriarVagaComQuantidadeNula() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", null, edital.getId());

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("vaga.quantidade.nao.informada"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void badRequestAoCriarVagaComQuantidadeZero() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 0, edital.getId());

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("vaga.quantidade.negativa"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void badRequestAoCriarVagaComIdEditalNull() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, null);

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("vaga.idEdital.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void badRequestAoCriarVagaComIdEditalInexistente() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, 2L);

        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("edital.nao.encontrado"), HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void forbiddenAoCriarVagaComTokenDeUsuarioAluno() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, edital.getId());
        enviarPostEValidarRespostaDeErro(vagaRequest, getMessageSource("usuario.sem.autorizacao"), HttpStatus.FORBIDDEN);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void sucessoAoBuscarVagasPorEditalId() throws Exception {

        Vaga vaga1 = new Vaga("Javascript", "2", 2, edital, usuario);
        vaga1 = vagaRepository.save(vaga1);

        Edital edital2 = new Edital("2022.1", LocalDate.of(2022, 2, 1), LocalDate.of(2022, 2, 15), usuario);
        edital2 = editalRepository.save(edital2);
        Vaga vaga2 = new Vaga("Programacao orientada a objetos", "3", 1, edital2, usuario);
        vagaRepository.save(vaga2);

        enviarGetPorEditalId(this.edital.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(vaga1.getId()))
                .andExpect(jsonPath("$[0].disciplina").value(vaga1.getDisciplina()))
                .andExpect(jsonPath("$[0].periodo").value(vaga1.getPeriodo()))
                .andExpect(jsonPath("$[0].quantidade").value(vaga1.getQuantidade()));
    }

    @Test
    void sucessoAoBuscarVagasVaziasPorEditalId() throws Exception {
        enviarGetPorEditalId(this.edital.getId())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void badRequestAoBuscarVagasPorEditalQueNaoExiste() throws Exception {
        enviarGetPorEditalIdEValidarRespostaDeErro(2L, getMessageSource("edital.nao.encontrado"), HttpStatus.BAD_REQUEST);

        List<Edital> editais = editalRepository.findAll();
        assertEquals(1, editais.size());
    }

}