package core.service;

import core.entity.Account;
import core.service.exception.EmailExistsException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Adrian on 10/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/prod/spring-context.xml")
@Transactional
public class AccountServiceTest {

    /*JUnit 4.7: Per-Test rules article:
      http://www.infoq.com/news/2009/07/junit-4.7-rules*/
    @Rule
    public ExpectedException exception = ExpectedException.none();



    @Autowired
    private AccountService accountService;

    Account firstAcc;

    @Before
    public void setup() throws Exception {
        Account acc = new Account();
        acc.setPassword("somepass");
        acc.setEmail("someemail");
        firstAcc = accountService.createAccount(acc);
    }

    @Test
    public void testFirstAccount() throws  Exception {
        assertNotNull(firstAcc);
        assertNotNull(firstAcc.getId());
        assertNotEquals("somepass", firstAcc.getPassword()); // Since it should be hashed
        assertEquals("someemail", firstAcc.getEmail());
    }

    @Test
    public void testCreateAccountWithTheSameEmail() throws Exception {
        Account newAcc = new Account();
        newAcc.setPassword("somepasswordz");
        newAcc.setEmail(firstAcc.getEmail());

        // Expect the EmailExistsException
        exception.expect(EmailExistsException.class);
        exception.expectMessage("Email already exists.");
        accountService.createAccount(newAcc);
    }
}
