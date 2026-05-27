package judgels.persistence.hibernate.dao;

import judgels.persistence.dao.BaseBundleItemSubmissionDao;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.model.AbstractBundleItemSubmissionModel;
import judgels.persistence.model.AbstractBundleItemSubmissionModel_;
import judgels.persistence.model.UnmodifiableModel_;
import org.hibernate.Session;

public abstract class AbstractBundleItemSubmissionHibernateDao<M extends AbstractBundleItemSubmissionModel> extends JudgelsHibernateDao<M> implements BaseBundleItemSubmissionDao<M> {
    public AbstractBundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public AbstractBundleItemSubmissionHibernateQueryBuilder<M> select() {
        return new AbstractBundleItemSubmissionHibernateQueryBuilder<>(currentSession(), getEntityClass());
    }

    @Override
    public void deleteAllByProblemJid(String problemJid) {
        throw new UnsupportedOperationException();
    }

    private static class AbstractBundleItemSubmissionHibernateQueryBuilder<M extends AbstractBundleItemSubmissionModel> extends HibernateQueryBuilder<M> implements BaseBundleItemSubmissionQueryBuilder<M> {
        AbstractBundleItemSubmissionHibernateQueryBuilder(Session currentSession, Class<M> entityClass) {
            super(currentSession, entityClass);
        }

        @Override
        public BaseBundleItemSubmissionQueryBuilder<M> whereContainerIs(String containerJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid));
            return this;
        }

        @Override
        public BaseBundleItemSubmissionQueryBuilder<M> whereAuthorIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        @Override
        public BaseBundleItemSubmissionQueryBuilder<M> whereProblemIs(String problemJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.problemJid, problemJid));
            return this;
        }

        @Override
        public BaseBundleItemSubmissionQueryBuilder<M> whereItemIs(String itemJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.itemJid, itemJid));
            return this;
        }
    }
}
