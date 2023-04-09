package br.com.monitoria.web.controller;

import br.com.monitoria.domain.*;
import br.com.monitoria.repository.*;
import br.com.monitoria.util.Paths;
import br.com.monitoria.web.request.InscricaoRequest;
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

import static br.com.monitoria.testUtils.UsuarioTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private MessageSource messageSource;

    public InscricaoControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.token = autenticarComAdmin(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("admin@gmail.com").get();
        this.edital = new Edital("2022.2", LocalDate.now(), LocalDate.now().plusDays(14), usuario);
        editalRepository.save(edital);
        this.vaga1 = new Vaga("Javascript", "2", 2, edital, usuario);
        this.vaga2 = new Vaga("Programacao orientada a objetos", "3", 1, edital, usuario);
        this.vaga3 = new Vaga("Programacao para web I", "4", 1, edital, usuario);
        vagaRepository.save(vaga1);
        vagaRepository.save(vaga2);
        vagaRepository.save(vaga3);
    }

    private String getMessageSource(String defaultMessage) {
        return messageSource.getMessage(defaultMessage, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    private ResultActions enviarPost(InscricaoRequest request) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post(Paths.INSCRICOES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private void enviarPostEValidarRespostaDeErro(InscricaoRequest request, String mensagemDeErro, HttpStatus httpStatus) throws Exception {
        enviarPost(request)
                .andExpect(status().is(httpStatus.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(httpStatus.value()))
                .andExpect(jsonPath("$.error").value(httpStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.INSCRICOES));
    }

    private ResultActions enviarGetProximaOpcao() throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.get(Paths.INSCRICOES + Paths.PROXIMA_OPCAO)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    public void enviarGetProximaOpcaoEValidarRespostaDeErro(String mensagemDeErro, HttpStatus httpStatus) throws Exception {
        enviarGetProximaOpcao()
                .andExpect(status().is(httpStatus.value()))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(httpStatus.value()))
                .andExpect(jsonPath("$.error").value(httpStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(mensagemDeErro))
                .andExpect(jsonPath("$.path").value(Paths.INSCRICOES + Paths.PROXIMA_OPCAO));
    }

    public ResultActions enviarGetResultados(String editalId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(Paths.INSCRICOES + Paths.RESULTADOS)
                .param("editalId", editalId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
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
    void badRequestAoCriarInscricaoComOpcaoNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(null, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.opcao.nao.informada"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComOpcaoAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(0, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.opcao.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComOpcaoAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(3, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.opcao.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComNotaDisciplinaNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, null, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.nota.nao.informada"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComNotaDisciplinaAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 69.9, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.nota.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComNotaDisciplinaAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 101.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.nota.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComCreNull() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, null, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.cre.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComCreAbaixoDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, -1.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.cre.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComCreAcimaDoRange() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 101.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.cre.range"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void badRequestAoCriarInscricaoComIdVagaNula() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, null);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.idVaga.nao.informado"), HttpStatus.BAD_REQUEST);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void forbiddenAoCriarInscricaoComUsuarioAdmin() throws Exception {
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.apenas.alunos"), HttpStatus.FORBIDDEN);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void forbiddenAoCriarInscricaoComUsuarioCoordenador() throws Exception {
        this.token = autenticarComCoordenador(objectMapper, mockMvc);
        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.apenas.alunos"), HttpStatus.FORBIDDEN);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void unprocessableEntityAoCriarPrimeiraInscricaoComOpcao2() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        InscricaoRequest inscricaoRequest = new InscricaoRequest(2, 85.0, 70.0, 1L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.primeira.opcao"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void unprocessableEntityAoCriarSegundaInscricaoComOpcao1() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();
        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoOpcao1 = inscricaoRepository.save(inscricaoOpcao1);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, vaga2.getId());

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.segunda.opcao"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertEquals(1, inscricoes.size());
        assertEquals(1, inscricoes.get(0).getOpcao());
    }

    @Test
    void unprocessableEntityAoCriarMaisQueDuasInscricoes() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();
        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoRepository.save(inscricaoOpcao1);

        Inscricao inscricaoOpcao2 = new Inscricao(2, 85.0, 75.0, 78.5, vaga2, usuario);
        inscricaoRepository.save(inscricaoOpcao2);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(2, 85.0, 70.0, 3L);

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.duas.opcoes"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertEquals(2, inscricoes.size());
    }

    @Test
    void unprocessableEntityAoCriarInscricaoAntesDaDataDeInicioDoEdital() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();

        vagaRepository.deleteAll();
        editalRepository.deleteAll();

        edital = new Edital("2022.2", LocalDate.now().plusDays(1), LocalDate.now().plusDays(14), usuario);
        edital = editalRepository.save(edital);

        vaga1 = new Vaga("Javascript", "2", 2, edital, usuario);
        vaga1 = vagaRepository.save(vaga1);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, vaga1.getId());

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.edital.ainda.fechado"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void unprocessableEntityAoCriarInscricaoDepoisDaDataDeFimDoEdital() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();

        vagaRepository.deleteAll();
        editalRepository.deleteAll();

        edital = new Edital("2022.2", LocalDate.now().minusDays(14), LocalDate.now().minusDays(1), usuario);
        edital = editalRepository.save(edital);

        vaga1 = new Vaga("Javascript", "2", 2, edital, usuario);
        vaga1 = vagaRepository.save(vaga1);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(1, 85.0, 70.0, vaga1.getId());

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.edital.ja.fechado"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    void retorno1CasoAlunoNaoTenhaNenhumaInscricao() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);

        enviarGetProximaOpcao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opcao").value("1"));
    }

    @Test
    void retorna2CasoAlunoJaTenhaUmaInscricao() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();

        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoRepository.save(inscricaoOpcao1);

        enviarGetProximaOpcao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opcao").value("2"));
    }

    @Test
    void retorna1NegativoCasoAlunoJaTenhaDuasInscricoes() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();

        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoRepository.save(inscricaoOpcao1);

        Inscricao inscricaoOpcao2 = new Inscricao(2, 85.0, 75.0, 78.5, vaga2, usuario);
        inscricaoRepository.save(inscricaoOpcao2);

        enviarGetProximaOpcao()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opcao").value("-1"));
    }

    @Test
    void unprocessableEntityCasoAlunoTenteSeInscreverNaMesmaVagaDuasVezes() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();

        Inscricao inscricaoOpcao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricaoRepository.save(inscricaoOpcao1);

        InscricaoRequest inscricaoRequest = new InscricaoRequest(2, 85.0, 70.0, vaga1.getId());

        enviarPostEValidarRespostaDeErro(inscricaoRequest, getMessageSource("inscricao.aluno.ja.inscrito"), HttpStatus.UNPROCESSABLE_ENTITY);

        List<Inscricao> inscricoes = inscricaoRepository.findAll();
        assertEquals(1, inscricoes.size());
    }

    @Test
    void unprocessableEntityCasoNaoExistaEditalAtivo() throws Exception {
        this.token = autenticarComAluno(objectMapper, mockMvc);

        vagaRepository.deleteAll();
        editalRepository.deleteAll();

        enviarGetProximaOpcaoEValidarRespostaDeErro(getMessageSource("edital.nenhum.cadastrado"), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void forbiddenAoAdminTentarAcessarEndpointDeProximaOpcao() throws Exception {
        enviarGetProximaOpcaoEValidarRespostaDeErro(getMessageSource("usuario.sem.autorizacao"), HttpStatus.FORBIDDEN);
    }

    @Test
    void forbiddenAoCoordenadorTentarAcessarEndpointDeProximaOpcao() throws Exception {
        this.token = autenticarComCoordenador(objectMapper, mockMvc);

        enviarGetProximaOpcaoEValidarRespostaDeErro(getMessageSource("usuario.sem.autorizacao"), HttpStatus.FORBIDDEN);
    }

    @Test
    void sucessoAoBuscarResultados() throws Exception {

        this.token = autenticarComAluno(objectMapper, mockMvc);
        this.usuario = usuarioRepository.findByLogin("aluno_01@gmail.com").get();
        Inscricao inscricao1 = new Inscricao(1, 85.0, 75.0, 78.5, vaga1, usuario);
        inscricao1 = inscricaoRepository.save(inscricao1);

        enviarGetResultados(this.edital.getId().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeAluno").value(inscricao1.getUsuario().getNome()))
                .andExpect(jsonPath("$[0].nota").value(inscricao1.getNotaDisciplina()))
                .andExpect(jsonPath("$[0].cre").value(inscricao1.getCre()))
                .andExpect(jsonPath("$[0].media").value(inscricao1.getMedia()))
                .andExpect(jsonPath("$[0].disciplina").value(inscricao1.getVaga().getDisciplina()))
                .andExpect(jsonPath("$[0].opcao").value(inscricao1.getOpcao()))
                .andExpect(jsonPath("$[0].resultado").value(inscricao1.getResultado().toString()));
    }

    @Test
    void sucessoAoBuscarResultadosVazio() throws Exception {
        enviarGetResultados(this.edital.getId().toString())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

}