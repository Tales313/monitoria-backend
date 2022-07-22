package br.com.monitoria.testUtils;

import br.com.monitoria.web.request.LoginRequest;
import br.com.monitoria.web.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class UsuarioTestUtils {

    public static String autenticarComAdmin(ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@gmail.com", "123456");
        return autenticarGenerico(loginRequest, mockMvc, objectMapper);
    }

    public static String autenticarComCoordenador(ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        LoginRequest loginRequest = new LoginRequest("coordenador_01@gmail.com", "123456");
        return autenticarGenerico(loginRequest, mockMvc, objectMapper);
    }

    public static String autenticarComAluno(ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        LoginRequest loginRequest = new LoginRequest("aluno_01@gmail.com", "123456");
        return autenticarGenerico(loginRequest, mockMvc, objectMapper);
    }

    private static String autenticarGenerico(LoginRequest loginRequest, MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseString, LoginResponse.class);
        return loginResponse.getToken();
    }

}
