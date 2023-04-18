package judgels.sandalphon.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.LessonPartnerModel;
import judgels.sandalphon.persistence.LessonPartnerModel_;

@Singleton
public final class LessonPartnerHibernateDao extends HibernateDao<LessonPartnerModel> implements LessonPartnerDao {

    @Inject
    public LessonPartnerHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.and(cb.equal(root.get(LessonPartnerModel_.lessonJid), lessonJid), cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid)));

        return currentSession().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public LessonPartnerModel findByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<LessonPartnerModel> query = cb.createQuery(getEntityClass());
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .where(cb.and(cb.equal(root.get(LessonPartnerModel_.lessonJid), lessonJid), cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid)));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<String> getLessonJidsByPartnerJid(String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .select(root.get(LessonPartnerModel_.lessonJid))
                .where(cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public Optional<LessonPartnerModel> selectByLessonJidAndUserJid(String lessonJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<LessonPartnerModel>()
                .putColumnsEq(LessonPartnerModel_.lessonJid, lessonJid)
                .putColumnsEq(LessonPartnerModel_.userJid, userJid)
                .build());
    }

    @Override
    public List<LessonPartnerModel> selectAllByLessonJid(String lessonJid) {
        return selectAll(new FilterOptions.Builder<LessonPartnerModel>()
                .putColumnsEq(LessonPartnerModel_.lessonJid, lessonJid)
                .build());
    }
}