package core.repository.impl.jpa;

import core.entity.Account;
import core.entity.Role;
import core.entity.Test;
import core.repository.AccountRepo;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Adrian on 10/05/2015.
 */
@Repository
@Transactional
public class AccountRepoImpl implements AccountRepo {

    /*@PersistenceContext
    private EntityManager emgr;*/

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Account findAccount(Long id) {
        /*return emgr.find(Account.class, id);*/
        Account acc = (Account) sessionFactory.getCurrentSession()
                .createCriteria(Account.class)
                .add(Restrictions.eq("id", id))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

        return acc;
    }

    @Override
    public Account findAccountByUsername(String username) {
        /*Query query = emgr.createQuery("SELECT a FROM Account a WHERE a.username = :username");
        query.setParameter("username", username);

        if (!query.getResultList().isEmpty())
            return (Account) query.getResultList().get(0);
        else
            return null;*/
        Account account = (Account) sessionFactory.getCurrentSession()
                .createCriteria(Account.class)
                .add(Restrictions.eq("username", username))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        return account;
    }

    @Override
    public Account findAccountByEmail(String email) {
        /*Query query = emgr.createQuery("SELECT a FROM Account a WHERE a.email = :email");
        query.setParameter("email", email);

        if (!query.getResultList().isEmpty())
            return (Account) query.getResultList().get(0);
        else
            return null;*/
        Account account = (Account) sessionFactory.getCurrentSession()
                .createCriteria(Account.class)
                .add(Restrictions.eq("email", email))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        return account;
    }

    @Override
    public Account createAccount(Account acc) {
        /*// Find the user role
        Query query = emgr.createQuery("SELECT r FROM Role r WHERE r.role = :role");
        query.setParameter("role", "ROLE_USER");

        if (query.getResultList().isEmpty())
            return null;

        Role role = (Role) query.getResultList().get(0);

        // Create new account roles obj
        Set<Role> accRoles = new HashSet<Role>();
        accRoles.add(role);
        acc.setAccRoles(accRoles);

        emgr.persist(acc);
        emgr.flush();

        // Attach the role to the acc obj
       *//* Set<Role> roles = new HashSet<Role>(0);
        roles.add(role);
        acc.setRoles(roles);*//*

        return acc;*/

        // Find the user role
        Role role = (Role) sessionFactory.getCurrentSession()
                .createCriteria(Role.class)
                .add(Restrictions.eq("role", "ROLE_USER"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        if (role == null)
            return null;

        // attach the roles to account obj
        Set<Role> accRoles = new HashSet<>();
        accRoles.add(role);
        acc.setAccRoles(accRoles);

        // create the new acc
        sessionFactory.getCurrentSession()
                .save(acc);
        sessionFactory.getCurrentSession().flush();

        return acc;
    }

    @Override
    public Account updateAccount(Account acc) {
        /*return emgr.merge(acc);*/
        sessionFactory.getCurrentSession()
                .saveOrUpdate(acc);
        sessionFactory.getCurrentSession()
                .flush();
        return acc;
    }

    @Override
    public Test getTest(Account acc) {
        /*Query query = emgr.createQuery("SELECT t FROM Test t WHERE t.acc =:acc");
        query.setParameter("acc", acc);

        if (query.getResultList().isEmpty())
            return null;

        Test test = (Test) query.getResultList().get(0);

        return test;*/
        Test test = (Test) sessionFactory.getCurrentSession()
                .createCriteria(Test.class)
                .add(Restrictions.eq("acc", acc))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        return test;
    }

    @Override
    public Test setTestWord(Account acc, String testWord) {
        /*Test test = getTest(acc);
        if (test == null) { // no record for this acc yet
            System.out.println("      GOTTA PERSIST NEW TEST");
            // persist
            test = new Test();
            test.setAcc(acc);
            test.setTest_word(testWord);
            emgr.persist(test);
            emgr.flush();
            System.out.println("               PERSISTED!");
        } else { // record exists for this acc
            System.out.println("          FOUND EXISTING RECORD, GONNA MERGE");
            test.setTest_word(testWord);
            emgr.merge(test);
            emgr.flush();
        }

        return test;*/

        Test test = getTest(acc);
        if (test == null) {
            test = new Test();
            test.setAcc(acc);
        }

        // update test word
        test.setTest_word(testWord);

        sessionFactory.getCurrentSession()
                .saveOrUpdate(test);
        sessionFactory.getCurrentSession()
                .flush();
        return test;
    }
}
