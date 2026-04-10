package com.haidong.tuanwei.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockHttpSession adminSession;

    @BeforeEach
    void setUp() throws Exception {
        loginAsAdmin();
    }

    private void loginAsAdmin() throws Exception {
        MvcResult loginResult = mockMvc.perform(formLogin("/perform_login")
                        .loginProcessingUrl("/login")
                        .user("username", "admin")
                        .password("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andReturn();

        HttpSession session = loginResult.getRequest().getSession(false);
        if (session instanceof MockHttpSession mockSession) {
            this.adminSession = mockSession;
        } else {
            throw new IllegalStateException("Failed to obtain MockHttpSession after login");
        }
    }

}
