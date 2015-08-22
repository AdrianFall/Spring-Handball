package core.controller;

import core.entity.Account;
import core.entity.VerificationToken;
import core.event.OnRegistrationCompleteEvent;
import core.service.AccountService;
import core.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Adrian on 10/05/2015.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/prod/spring-context.xml")
@Transactional
public class AccountControllerTest {

    private static final String REGISTRATION_FORM_MODEL = "registrationForm";
    private static final String LOGIN_FORM_MODEL = "loginForm";

    @InjectMocks
    AccountController controller;

    @Mock
    private AccountService accountService;
    private EmailService emailService;



    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .alwaysExpect(status().isOk())
                .build();
    }

    @Test
    public void testMockMvcIsNotNull() throws Exception {
        assertNotNull(mockMvc);
    }

    @Test
    public void testGetAccountForm() throws Exception {
        mockMvc.perform(get("/register.jsp"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/registrationForm.jsp"));
    }

    @Test
    public void testGetLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAccountWithEmptyParams() throws Exception {
       /* when(mockBindingResult.hasErrors()).thenReturn(true);*/

        mockMvc.perform(postForm("", "", ""))
                .andDo(print())
                // Model errors
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors(REGISTRATION_FORM_MODEL))
                //.andExpect(model().attributeErrorCount(REGISTRATION_FORM_MODEL, 3))
                .andExpect(model().attributeHasFieldErrors(REGISTRATION_FORM_MODEL, "password", "email"));

                /*.andExpect(view().name(REGISTRATION_FORM_MODEL))
                .andExpect(model().hasErrors()*//*attributeHasErrors(REGISTRATION_FORM_MODEL, "username", "password", "email")*//*);*/
    }

    @Test
    public void testCreateAccountWithWrongEmailPattern() throws Exception {
        String username = "username";
        String password = "passww";
        String email = "no@dot";

        mockMvc.perform(postForm(password, password, email))
                .andDo(print())
                // Model errors
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors(REGISTRATION_FORM_MODEL))
                //.andExpect(model().attributeErrorCount(REGISTRATION_FORM_MODEL, 2))
                .andExpect(model().attributeHasFieldErrors(REGISTRATION_FORM_MODEL, "email"));
    }

    @Test
    public void testCreateAccountWithMismatchingPassword() throws Exception {
        String username = "testuser";
        String password = "somepassword";
        String confirmPassword = "somepaszword";
        String email = "email@pattern.pl";

        mockMvc.perform(postForm(password, confirmPassword, email))
                .andDo(print())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasErrors(REGISTRATION_FORM_MODEL))
                .andExpect(model().attributeHasFieldErrors(REGISTRATION_FORM_MODEL, "password", "confirmPassword"));


    }

    private MockHttpServletRequestBuilder postForm(String password, String confirmPassword, String email) {
        return post("/register.jsp")
                .param("email", email)
                .param("password", password)
                .param("confirmPassword", confirmPassword);
    }


}
