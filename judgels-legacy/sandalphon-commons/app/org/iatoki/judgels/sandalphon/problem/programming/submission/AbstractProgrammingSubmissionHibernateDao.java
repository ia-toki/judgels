package org.iatoki.judgels.sandalphon.problem.programming.submission;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.play.model.AbstractJudgelsModel_;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractProgrammingSubmissionHibernateDao<M extends AbstractProgrammingSubmissionModel> extends AbstractJudgelsHibernateDao<M> implements BaseProgrammingSubmissionDao<M> {

    protected AbstractProgrammingSubmissionHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public List<M> getByContainerJidSinceTime(String containerJid, long time) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.le(root.get(AbstractProgrammingSubmissionModel_.timeCreate), time)));
        query.orderBy(cb.asc(root.get(AbstractProgrammingSubmissionModel_.timeCreate)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.userCreate), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));
        query.orderBy(cb.asc(root.get(AbstractProgrammingSubmissionModel_.timeCreate)));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public long countByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.userCreate), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public List<Long> getAllSubmissionsSubmitTime() {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query.select(root.get(AbstractJudgelsModel_.timeCreate));

        return JPA.em().createQuery(query).getResultList();
    }
}
