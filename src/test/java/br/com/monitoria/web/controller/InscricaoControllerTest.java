package br.com.monitoria.web.controller;

import br.com.monitoria.domain.*;
import br.com.monitoria.repository.*;
import br.com.monitoria.web.request.InscricaoRequest;
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

import static br.com.monitoria.testUtils.UsuarioTestUtils.*;
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

    private String token;

    private Usuario usuario;

    private Edital edital;

    private Vaga vaga1;
    private Vaga vaga2;
    private Vaga vaga3;

    public InscricaoControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.token = autenticarComAdmin(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("admin@gmail.com").get();
        this.edital = new Edital("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15), usuario);
        editalRepository.save(edital);
        this.vaga1 = new Vaga("Javascript", "2", 2, edital, usuario);
        this.vaga2 = new Vaga("Programacao orientada a objetos", "3", 1, edital, usuario);
        this.vaga3 = new Vaga("Programacao para web I", "4", 1, edital, usuario);
        vagaRepository.save(vaga1);
        vagaRepository.save(vaga2);
        vagaRepository.save(vaga3);
    }

    private ResultActions enviarPost(InscricaoRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/inscricoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarRespostaDeErro(InscricaoRequest request, String mensagemDeErro, HttpStatus httpStatus) throws Exception {
        enviarPost(request)
                .andExpect(status().is(httpStatus.value()))
                .andExpect(jsonPath("$.status").value(httpStatus.value()))
                .andExpect(jsonPath("$.error").value(httpStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro));
    }

    @Test
    void sucessoAoCriarInscricao() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPost(inscricaoRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.dataInscricao").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.opcao").value(1))
                .andExpect(jsonPath("$.notaDisciplina").value(85.0))
                .andExpect(jsonPath("$.cre").value(70.0))
                .andExpect(jsonPath("$.media").value(80.5))
                .andExpect(jsonPath("$.resultado").value(ResultadoEnum.AGUARDANDO.toString()));

        Optional<Inscricao> optionalInscricao = inscricaoRepository.findById(1L);

        assertTrue(optionalInscricao.isPresent());

        Inscricao inscricao = optionalInscricao.get();

        assertEquals(1L, inscricao.getId());
        assertEquals(LocalDate.now(), inscricao.getDataInscricao());
        assertEquals(1, inscricao.getOpcao());
        assertEquals(85.0, inscricao.getNotaDisciplina());
        assertEquals(70.0, inscricao.getCre());
        assertEquals(80.5, inscricao.getMedia());
        assertEquals(ResultadoEnum.AGUARDANDO, inscricao.getResultado());
        assertEquals(1L, inscricao.getVaga().getId());
        assertEquals("aluno_01@gmail.com", inscricao.getUsuario().getLogin());
    }

    @Test
    void erroAoCriarInscricaoComOpcaoNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(null, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A opcao deve ser informada", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComOpcaoAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(0, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A opcao deve ser 1 ou 2", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComOpcaoAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(3, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A opcao deve ser 1 ou 2", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComNotaDisciplinaNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, null, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A nota da disciplina deve ser informada", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComNotaDisciplinaAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 69.9, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A nota da disciplina deve ser entre 70 e 100", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComNotaDisciplinaAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 101.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "A nota da disciplina deve ser entre 70 e 100", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComCreNull() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, null, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "O CRE deve ser informado", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComCreAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, -1.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "O CRE deve ser entre 0 e 100", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComCreAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 101.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "O CRE deve ser entre 0 e 100", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComIdVagaNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, null);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "O id da vaga deve ser informado", HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComUsuarioAdmin() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "Apenas alunos podem se inscrever para concorrer a monitoria.", HttpStatus.FORBIDDEN);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void erroAoCriarInscricaoComUsuarioCoordenador() throws Exception {
        this.token = autenticarComCoordenador(objectMapper, mockMvc);
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "Apenas alunos podem se inscrever para concorrer a monitoria.", HttpStatus.FORBIDDEN);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void unprocessableEntityAoTentarCriarPrimeiraInscricaoComOpcao2() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        InscricaoRequest inscricaoRequest = new InscricaoRequest(2, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "Sua primeira inscrição deve ser a opção 1", HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void unprocessableEntityAoTentarCriarSegundaInscricaoComOpcao1() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();
        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoOpcao1 = inscricaoRepository.save(inscricaoOpcao1);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "Sua segunda inscrição deve ser a opção 2", HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertEquals(1, inscricoes.size());
        assertEquals(1, inscricoes.get(0).getOpcao());
    }

    @Test
    void unprocessableEntityAoTentarCriarMaisQueDuasInscricoes() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();
        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoOpcao1 = inscricaoRepository.save(inscricaoOpcao1);

        Inscricao inscricaoOpcao2 = new Inscricao(2, 85.0, 75.0, 78.5, vaga2, usuario);
        inscricaoOpcao2 = inscricaoRepository.save(inscricaoOpcao2);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(2, 85.0, 70.0, 3L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, "Você não pode ter mais que duas inscrições", HttpStatus.UNPROCESSABLE_ENTITY);
    }

}