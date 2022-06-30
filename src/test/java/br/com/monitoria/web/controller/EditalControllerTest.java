package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.web.request.EditalRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static br.com.monitoria.testUtils.UsuarioTestUtils.criarUsuarioEAutenticar;


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

    public EditalControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do EditalRequest. Sem essa linha ele da erro de jackson.
    }

    @BeforeEach
    void setUp() throws Exception {
        this.token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
    }

    private ResultActions enviarPost(EditalRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/editais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarMensagemDeBadRequest(EditalRequest request, String mensagemDeErro) throws Exception {
        enviarPost(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
    }

    private ResultActions enviarGet(Long id) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get("/editais/" + id)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void sucessoAoTentarCriarEdital() throws Exception {

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
    void erroAoCriarEditalComSemestreNulo() throws Exception {
        EditalRequest editalRequest = new EditalRequest(null, LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "O semestre deve ser informado");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void erroAoCriarEditalComSemestreEmBranco() throws Exception {
        EditalRequest editalRequest = new EditalRequest("", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "O semestre deve ter o formato 2022.1");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void erroAoCriarEditalComSemestreInvalido() throws Exception {
        EditalRequest editalRequest = new EditalRequest("123.4", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "O semestre deve ter o formato 2022.1");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void erroAoCriarEditalComInicioInscricoesNula() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", null, LocalDate.of(2022, 7, 15));

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "A data de inicio das incrições deve ser informada");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void erroAoCriarEditalComFimInscricoesNula() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", LocalDate.of(2022, 7, 1), null);

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "A data de fim das inscrições deve ser informada");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void erroAoCriarEditalComDataInicioInscricoesAposDataFimInscricoes() throws Exception {
        EditalRequest editalRequest = new EditalRequest("2022.1", LocalDate.of(2022, 7, 15), LocalDate.of(2022, 7, 1));

        enviarPostEValidarMensagemDeBadRequest(editalRequest, "A data de início das inscrições deve ser antes da data de fim das incrições");

        List<Edital> editais = editalRepository.findAll();
        assertTrue(editais.isEmpty());
    }

    @Test
    void buscarEditalComSucesso() throws Exception {
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        edital = editalRepository.save(edital);

        enviarGet(edital.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.semestre").value("2022.2"))
                .andExpect(jsonPath("$.inicioInscricoes").value("2022-07-01"))
                .andExpect(jsonPath("$.fimInscricoes").value("2022-07-15"));
    }

    @Test
    void erroAoBuscarEditalNaoExistente() throws Exception {

        enviarGet(1L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Edital não encontrado"));
    }

}