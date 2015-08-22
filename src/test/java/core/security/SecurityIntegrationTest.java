package core.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Adrian on 11/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/prod/*") // Requires both security-context and application-context (containing auth bean 'accountDetailsService')
public class SecurityIntegrationTest {

    @Test
    public void contextConfigShouldWork() throws Exception {}
}
