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
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "BEHEERDER")
    void getAllBooks_withBeheerderRole_returns200() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "KLANT")
    void getAllBooks_withKlantRole_returns200() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBooks_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "KLANT")
    void createBook_withKlantRole_returns403() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "isbn": "978-0-0000-0000-0",
                                    "title": "Test Boek",
                                    "publisher": "Test",
                                    "publicationYear": 2024,
                                    "category": "Test",
                                    "description": "Test",
                                    "coverImagePath": null,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BEHEERDER")
    void getBookById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/books/9999"))
                .andExpect(status().isNotFound());
    }
}