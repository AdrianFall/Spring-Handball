package core.service.impl;

import core.entity.Account;
import core.entity.PasswordResetToken;
import core.entity.Test;
import core.entity.VerificationToken;
import core.repository.AccountRepo;
import core.repository.PasswordResetTokenRepo;
import core.repository.VerificationTokenRepo;
import core.service.AccountService;
import core.service.exception.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Adrian on 10/05/2015.
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private VerificationTokenRepo tokenRepo;

    @Autowired
    private PasswordResetTokenRepo passwordResetRepo;


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Test getTest(Account acc) {
        return accountRepo.getTest(acc);
    }

    @Override
    public Test setTest(Account acc, String testWord) {
        return accountRepo.setTestWord(acc, testWord);
    }

    @Override
    public Account createAccount(Account acc) throws EmailExistsException {
        if (accountRepo.findAccountByEmail(acc.getEmail()) != null) {
            throw new EmailExistsException("Email already exists.");
        }

        // Hash the password
        acc.setPassword(passwordEncoder.encode(acc.getPassword()));

        return accountRepo.createAccount(acc);
    }

    @Override
    public Account updateAccount(Account acc) {
        return accountRepo.updateAccount(acc);
    }

    @Override
    public VerificationToken createVerificationToken(Account acc, String token) {
        return tokenRepo.createVerificationToken(new VerificationToken(token, acc));
    }

    @Override
    public VerificationToken findVerificationToken(String token) {
        return tokenRepo.findVerificationToken(token);
    }

    @Override
    public Account findAccount(String email) {
        return accountRepo.findAccountByEmail(email);
    }

    @Override
    public VerificationToken updateVerificationToken(VerificationToken newToken, Account acc) {
        return tokenRepo.updateVerificationToken(newToken, acc);
    }

    @Override
    public PasswordResetToken createPasswordResetToken(Account acc, String token) {
        return passwordResetRepo.createPasswordResetToken(new PasswordResetToken(token,acc));
    }

    @Override
    public PasswordResetToken findPasswordResetToken(String token) {
        return passwordResetRepo.findPasswordResetToken(token);
    }

}
