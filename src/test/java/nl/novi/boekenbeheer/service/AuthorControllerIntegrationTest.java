package nl.novi.boekenbeheer.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "BEHEERDER")
    void createAuthor_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Integratie",
                                    "lastName": "Test"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Integratie"))
                .andExpect(jsonPath("$.lastName").value("Test"));
    }

    @Test
    @WithMockUser(roles = "KLANT")
    void createAuthor_withKlantRole_returns403() throws Exception {
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Integratie",
                                    "lastName": "Test"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BEHEERDER")
    void getAllAuthors_withValidRole_returns200() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllAuthors_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isUnauthorized());
    }
}