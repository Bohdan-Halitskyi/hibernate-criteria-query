package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone of " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneCriteriaQuery
                    = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = phoneCriteriaQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            params.forEach((param, values) -> {
                CriteriaBuilder.In<String> predicate
                        = criteriaBuilder.in(root.get(param));
                for (String value : values) {
                    predicate.value(value);
                }
                predicates.add(predicate);
            });
            Predicate resultPredicate
                    = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            phoneCriteriaQuery.where(resultPredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phone by parameters "
                    + params, e);
        }
    }
}
