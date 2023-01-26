package judgels.sandalphon.hibernate;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;

@Singleton
public final class ProblemPartnerHibernateDao extends HibernateDao<ProblemPartnerModel> implements ProblemPartnerDao {

    @Inject
    public ProblemPartnerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByProblemJidAndPartnerJid(String problemJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ProblemPartnerModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.and(cb.equal(root.get(ProblemPartnerModel_.problemJid), problemJid), cb.equal(root.get(ProblemPartnerModel_.userJid), partnerJid)));

        return currentSession().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public ProblemPartnerModel findByProblemJidAndPartnerJid(String problemJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ProblemPartnerModel> query = cb.createQuery(getEntityClass());
        Root<ProblemPartnerModel> root = query.from(getEntityClass());

        query
                .where(cb.and(cb.equal(root.get(ProblemPartnerModel_.problemJid), problemJid), cb.equal(root.get(ProblemPartnerModel_.userJid), partnerJid)));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<String> getProblemJidsByPartnerJid(String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<ProblemPartnerModel> root = query.from(getEntityClass());

        query
                .select(root.get(ProblemPartnerModel_.problemJid))
                .where(cb.equal(root.get(ProblemPartnerModel_.userJid), partnerJid));

        return currentSession().createQuery(query).getResultList();
    }
}
