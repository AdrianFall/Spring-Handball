package af.rest.mvc;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import core.controller.IndexController;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Adrian on 09/05/2015.
 */
public class IndexControllerTest {

    private MockMvc mockMvc;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(new IndexController())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    public void testIndexControllerGet() throws Exception{
        /*MvcResult result =*/ mockMvc.perform(get("/index.jsp"))
                .andDo(print())
                .andExpect(forwardedUrl("/WEB-INF/jsp/index.jsp"))
                .andExpect(status().isOk())
                .andReturn();
        //String content = result.getResponse().getContentAsString();

        //assertNotNull(content);

    }




}
