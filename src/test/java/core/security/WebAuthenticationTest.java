package core.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Adrian on 13/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/prod/*") // Requires both security-context and application-context (containing auth bean 'accountDetailsService')
@WebAppConfiguration
public class WebAuthenticationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private static final String VALID_EMAIL = "adrianq92@hotmail.com";
    private static final String VALID_PASSWORD = "adiadi";


    @Before
    public void setup() throws Exception{
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()) // will perform all of the initial setup we need to integrate Spring Security with Spring MVC Test
                .build();
    }

    @Test
    public void shouldLoginWithStatusOk() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", VALID_EMAIL)
                .param("password", VALID_PASSWORD)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

/*    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/login").param("j_username",VALID_EMAIL).param("j_password",VALID_PASSWORD)).andExpect(status().isOk());
    }*/

/*    @Test
    public void wrongPasswordLogin() throws Exception {
        *//*mockMvc.perform(postLogin(VALID_EMAIL, "invalid"))
                .andExpect(unauthenticated());*//*

    }

    @Test
    public void withNoCsrfToken() throws Exception {
        mockMvc.perform(postLogin("adrianq92@hotmail.com", "adrianq"))
                .andExpect(unauthenticated());
    }

    @Test
    public void withInvalidCsrfToken() throws Exception {
        mockMvc.perform(postLogin(VALID_EMAIL, VALID_PASSWORD)
                        .with(csrf().useInvalidToken()))
                .andExpect(unauthenticated());
    }

    @Test
    public void authenticatedLogin() throws Exception {
        mockMvc.perform(postLogin(VALID_EMAIL, VALID_PASSWORD)
                .with(csrf()))
                .andExpect(authenticated());

    }

    private MockHttpServletRequestBuilder postLogin(String email, String password) {
        return post("/login").param("email", email).param("password", password);
    }*/
}
