package br.com.monitoria.testUtils;

import br.com.monitoria.domain.Usuario;
import br.com.monitoria.repository.UsuarioRepository;
import br.com.monitoria.web.request.LoginRequest;
import br.com.monitoria.web.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UsuarioTestUtils {

    public static String criarUsuarioEAutenticar(UsuarioRepository usuarioRepository, ObjectMapper objectMapper, MockMvc mockMvc) throws Exception {
        Usuario usuario = new Usuario("teste@gmail.com", "$2a$10$F/f76piJUaBdGsDlJ9dHD.yyxOUSWMY/bYob3Kwqx9whgIJ3hP1pu", "20171370011");
        usuarioRepository.save(usuario);

        LoginRequest loginRequest = new LoginRequest("teste@gmail.com", "123456");
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
