package core.repository;

import core.entity.Account;
import core.entity.Test;
import core.entity.VerificationToken;

/**
 * Created by Adrian on 10/05/2015.
 */
public interface AccountRepo {

    public Account findAccount(Long id);
    public Account findAccountByUsername(String username);
    public Account findAccountByEmail(String email);
    public Account createAccount(Account acc);
    public Account updateAccount(Account acc);
    public Test getTest(Account acc);
    public Test setTestWord(Account acc, String testWord);
}
