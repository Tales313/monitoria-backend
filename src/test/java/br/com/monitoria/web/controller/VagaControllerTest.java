package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.domain.Usuario;
import br.com.monitoria.domain.Vaga;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.web.request.VagaRequest;
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

    public VagaControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    private ResultActions enviarPost(VagaRequest request, String token) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarMensagemDeBadRequest(VagaRequest request, String token, String mensagemDeErro) throws Exception {
        enviarPost(request, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
    }

    @Test
    void sucessoAoCriarVaga() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, edital.getId());

        enviarPost(vagaRequest, token)
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
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("", "2", 2, edital.getId());

        enviarPostEValidarMensagemDeBadRequest(vagaRequest, token, "A disciplina deve ser informada");

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComPeriodoEmBranco() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "", 2, edital.getId());

        enviarPostEValidarMensagemDeBadRequest(vagaRequest, token, "O periodo deve ser informado");

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComQuantidadeNula() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", null, edital.getId());

        enviarPostEValidarMensagemDeBadRequest(vagaRequest, token, "A quantidade deve ser informada");

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComQuantidadeZero() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 0, edital.getId());

        enviarPostEValidarMensagemDeBadRequest(vagaRequest, token, "A quantidade deve ter valor positivo");

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }

    @Test
    void erroAoCriarVagaComIdEditalNull() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);

        VagaRequest vagaRequest = new VagaRequest("Javascript", "2", 2, null);

        enviarPostEValidarMensagemDeBadRequest(vagaRequest, token, "O id do edital deve ser informado");

        List<Vaga> vagas = vagaRepository.findAll();
        assertTrue(vagas.isEmpty());
    }
}