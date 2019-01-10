package org.iatoki.judgels.sandalphon.client.problem;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
@Named("clientProblemDao")
public final class ClientProblemHibernateDao extends AbstractHibernateDao<Long, ClientProblemModel> implements ClientProblemDao {

    public ClientProblemHibernateDao() {
        super(ClientProblemModel.class);
    }

    @Override
    public boolean existsByClientJidAndProblemJid(String clientJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ClientProblemModel> root = query.from(ClientProblemModel.class);

        query
                .select(cb.count(root))
                .where(cb.and(cb.equal(root.get(ClientProblemModel_.problemJid), problemJid), cb.equal(root.get(ClientProblemModel_.clientJid), clientJid)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public ClientProblemModel findByClientJidAndProblemJid(String clientJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ClientProblemModel> query = cb.createQuery(ClientProblemModel.class);
        Root<ClientProblemModel> root = query.from(ClientProblemModel.class);

        query
            .where(cb.and(cb.equal(root.get(ClientProblemModel_.problemJid), problemJid), cb.equal(root.get(ClientProblemModel_.clientJid), clientJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public List<ClientProblemModel> getByProblemJid(String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ClientProblemModel> query = cb.createQuery(ClientProblemModel.class);
        Root<ClientProblemModel> root = query.from(ClientProblemModel.class);

        query
            .where(cb.equal(root.get(ClientProblemModel_.problemJid), problemJid));

        return JPA.em().createQuery(query).getResultList();
    }
}
