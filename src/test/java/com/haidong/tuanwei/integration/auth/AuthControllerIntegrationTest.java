package com.haidong.tuanwei.integration.auth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

/**
 * 认证模块集成测试
 */
class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Test
    void loginPageShouldLoad() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void successfulLoginShouldRedirectToDashboard() throws Exception {
        mockMvc.perform(formLogin("/perform_login")
                        .loginProcessingUrl("/login")
                        .user("username", "admin")
                        .password("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void failedLoginShouldRedirectToLoginWithError() throws Exception {
        mockMvc.perform(formLogin("/perform_login")
                        .loginProcessingUrl("/login")
                        .user("username", "admin")
                        .password("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void logoutShouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/logout").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void accessingProtectedResourceWithoutAuthShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
