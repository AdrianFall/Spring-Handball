package core.repository.impl.jpa;

import core.entity.Account;
import core.entity.VerificationToken;
import core.repository.VerificationTokenRepo;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by Adrian on 14/05/2015.
 */
@Repository
@Transactional
public class VerificationTokenRepoImpl implements VerificationTokenRepo {

    /*@PersistenceContext
    private EntityManager emgr;*/

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public VerificationToken createVerificationToken(VerificationToken token) {
        /*emgr.persist(token);
        emgr.flush();
        return token;*/
        sessionFactory.getCurrentSession()
                .saveOrUpdate(token);
        sessionFactory.getCurrentSession()
                .flush();
        return token;
    }

    @Override
    public VerificationToken findVerificationToken(String verificationToken) {
        /*Query query = emgr.createQuery("SELECT v FROM verification_token v WHERE v.token = :token");
        query.setParameter("token", verificationToken);
        if (!query.getResultList().isEmpty())
            return (VerificationToken) query.getResultList().get(0);
        else
            return null;*/

        VerificationToken verToken = (VerificationToken) sessionFactory.getCurrentSession()
                .createCriteria(VerificationToken.class)
                .add(Restrictions.eq("token", verificationToken))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
        return verToken;
    }

    @Override
    public VerificationToken updateVerificationToken(VerificationToken newToken, Account acc) {
        /*Query query = emgr.createQuery("SELECT v FROM verification_token v WHERE v.acc = :account_id");
        query.setParameter("account_id", acc);
        if (!query.getResultList().isEmpty()) {
            VerificationToken tokenToBeUpdated = (VerificationToken) query.getResultList().get(0);
            tokenToBeUpdated.setToken(newToken.getToken());
            tokenToBeUpdated.setExpiryDate(newToken.getExpiryDate());
            return emgr.merge(tokenToBeUpdated);
        } else
            return null;*/

        VerificationToken tokenToBeUpdated = (VerificationToken) sessionFactory.getCurrentSession()
                .createCriteria(VerificationToken.class)
                .add(Restrictions.eq("acc", acc))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

        if (tokenToBeUpdated != null) {
            tokenToBeUpdated.setToken(newToken.getToken());
            tokenToBeUpdated.setExpiryDate(newToken.getExpiryDate());
            sessionFactory.getCurrentSession()
                    .saveOrUpdate(tokenToBeUpdated);
            sessionFactory.getCurrentSession()
                    .flush();
        }

        return tokenToBeUpdated;
    }
}
