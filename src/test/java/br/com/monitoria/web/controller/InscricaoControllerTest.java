package br.com.monitoria.web.controller;

import br.com.monitoria.domain.*;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.InscricaoRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.repository.VagaRepository;
import br.com.monitoria.web.request.InscricaoRequest;
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
import java.util.Optional;

import static br.com.monitoria.testUtils.UsuarioTestUtils.criarUsuarioEAutenticar;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InscricaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EditalRepository editalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    private ObjectMapper objectMapper;

    public InscricaoControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    private ResultActions enviarPost(InscricaoRequest request, String token) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/inscricoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarMensagemDeBadRequest(InscricaoRequest request, String token, String mensagemDeErro) throws Exception {
        enviarPost(request, token)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
    }

    @Test
    void sucessoAoCriarInscricao() throws Exception {
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);
        Usuario usuario = usuarioRepository.findByLogin("teste@gmail.com").get();
        Edital edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);
        Vaga vaga = new Vaga("Javascript", "2", 2, edital, usuario);
        vagaRepository.save(vaga);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 8.5, 7.0, 1L);

        enviarPost(inscricaoRequest, token)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.dataInscricao").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.opcao").value(1))
                .andExpect(jsonPath("$.notaDisciplina").value(8.5))
                .andExpect(jsonPath("$.cre").value(7.0))
                .andExpect(jsonPath("$.media").value(8.05))
                .andExpect(jsonPath("$.resultado").value(ResultadoEnum.AGUARDANDO.toString()));

        Optional<Inscricao> optionalInscricao = inscricaoRepository.findById(1L);

        assertTrue(optionalInscricao.isPresent());

        Inscricao inscricao = optionalInscricao.get();

        assertEquals(1L, inscricao.getId());
        assertEquals(LocalDate.now(), inscricao.getDataInscricao());
        assertEquals(1, inscricao.getOpcao());
        assertEquals(8.5, inscricao.getNotaDisciplina());
        assertEquals(7.0, inscricao.getCre());
        assertEquals(8.05, inscricao.getMedia());
        assertEquals(ResultadoEnum.AGUARDANDO, inscricao.getResultado());
        assertEquals(1L, inscricao.getVaga().getId());
        assertEquals(1L, inscricao.getUsuario().getId());
    }

}