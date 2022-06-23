package br.com.monitoria.web.controller;

import br.com.monitoria.domain.Edital;
import br.com.monitoria.repository.EditalRepository;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.web.request.EditalRequest;
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

    public EditalControllerTest() {
        this.objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        // Isso eh para que o spring consiga receber os dados do tipo LocalDate pelo
        // request do EditalRequest. Sem essa linha ele da erro de jackson.
    }


    private ResultActions enviarPost(EditalRequest request, String token) throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders.post("/editais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    void sucessoAoTentarCriarEdital() throws Exception {

        EditalRequest editalRequest = new EditalRequest("2022.2", LocalDate.of(2022, 7, 1), LocalDate.of(2022, 7, 15));
        String token = criarUsuarioEAutenticar(usuarioRepository, objectMapper, mockMvc);

        enviarPost(editalRequest, token)
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
    }

}