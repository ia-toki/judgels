package org.iatoki.judgels.sandalphon.client.lesson;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
@Named("clientLessonDao")
public final class ClientLessonHibernateDao extends AbstractHibernateDao<Long, ClientLessonModel> implements ClientLessonDao {

    public ClientLessonHibernateDao() {
        super(ClientLessonModel.class);
    }

    @Override
    public boolean existsByClientJidAndLessonJid(String clientJid, String lessonJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ClientLessonModel> root = query.from(ClientLessonModel.class);

        query
                .select(cb.count(root))
                .where(cb.and(cb.equal(root.get(ClientLessonModel_.lessonJid), lessonJid), cb.equal(root.get(ClientLessonModel_.clientJid), clientJid)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public ClientLessonModel findByClientJidAndLessonJid(String clientJid, String lessonJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ClientLessonModel> query = cb.createQuery(ClientLessonModel.class);
        Root<ClientLessonModel> root = query.from(ClientLessonModel.class);

        query
            .where(cb.and(cb.equal(root.get(ClientLessonModel_.lessonJid), lessonJid), cb.equal(root.get(ClientLessonModel_.clientJid), clientJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public List<ClientLessonModel> getByLessonJid(String lessonJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ClientLessonModel> query = cb.createQuery(ClientLessonModel.class);
        Root<ClientLessonModel> root = query.from(ClientLessonModel.class);

        query
            .where(cb.equal(root.get(ClientLessonModel_.lessonJid), lessonJid));

        return JPA.em().createQuery(query).getResultList();
    }
}
