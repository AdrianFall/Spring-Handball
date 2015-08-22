package core.repository;

import core.entity.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Adrian on 10/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/prod/spring-context.xml")
@Transactional

public class AccountRepoTest {

    @Autowired
    private AccountRepo repo;

    // First account
    private Account firstAcc;

    @Before
    @Rollback(true)
    public void setup() throws  Exception{
        firstAcc = new Account();
        firstAcc.setEmail("xa@xa");
        firstAcc.setPassword("superhard");
        repo.createAccount(firstAcc);
    }

    @Test
    public void findTheFirstAccountById() throws Exception {
        assertNotNull(repo.findAccount(firstAcc.getId()));
    }

    @Test
    public void findAccountByEmail() throws Exception {
        assertNotNull(repo.findAccountByEmail(firstAcc.getEmail()));
    }

    @Test
    public void updateAccount() throws Exception {
        // Modify the first acc
        firstAcc.setEmail("re@re.re");
        assertNotNull(repo.updateAccount(firstAcc));
        assertEquals("re@re.re", firstAcc.getEmail());
    }

}
