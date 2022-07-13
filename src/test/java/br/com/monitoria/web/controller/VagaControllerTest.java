package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.web.request.VagaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.monitoria.testUtils.UsuarioTestUtils.criarUsuarioEAutenticar;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    public VagaControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() throws Exception {
        this.token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        this.edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);
    }

    private ResultActions enviarPost(VagaRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarErro(VagaRequest request, String mensagemDeErro, HttpStatus status) throws Exception {
        enviarPost(request)
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.error").value(status.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
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
    void erroAoCriarVagaComDisciplinaEmBranco() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("", "2", 2, edital.getId());

        enviarPostEValidarErro(vagaRequest, "A disciplina deve ser informada", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComPeriodoEmBranco() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "", 2, edital.getId());

        enviarPostEValidarErro(vagaRequest, "O periodo deve ser informado", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComQuantidadeNula() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", null, edital.getId());

        enviarPostEValidarErro(vagaRequest, "A quantidade deve ser informada", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComQuantidadeZero() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 0, edital.getId());

        enviarPostEValidarErro(vagaRequest, "A quantidade deve ter valor positivo", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComIdEditalNull() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, null);

        enviarPostEValidarErro(vagaRequest, "O id do edital deve ser informado", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComIdEditalInexistente() throws Exception {
        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, 2L);

        enviarPostEValidarErro(vagaRequest, "Edital não encontrado", HttpStatus.BAD_REQUEST);

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }
}