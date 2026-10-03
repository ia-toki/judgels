package judgels.persistence.dao;

import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.model.AbstractBundleItemSubmissionModel;
import judgels.persistence.model.AbstractBundleItemSubmissionModel_;
import org.hibernate.Session;

public abstract class BaseBundleItemSubmissionDao<M extends AbstractBundleItemSubmissionModel> extends JudgelsDao<M> {
    public BaseBundleItemSubmissionDao(DaoData data) {
        super(data);
    }

    public abstract M createSubmissionModel();

    @Override
    public BaseBundleItemSubmissionQueryBuilder<M> select() {
        return new BaseBundleItemSubmissionQueryBuilder<>(currentSession(), getEntityClass());
    }

    public void deleteAllByProblemJid(String problemJid) {
        throw new UnsupportedOperationException();
    }

    public static class BaseBundleItemSubmissionQueryBuilder<M extends AbstractBundleItemSubmissionModel> extends QueryBuilder<M> {
        BaseBundleItemSubmissionQueryBuilder(Session currentSession, Class<M> entityClass) {
            super(currentSession, entityClass);
        }

        public BaseBundleItemSubmissionQueryBuilder<M> whereContainerIs(String containerJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid));
            return this;
        }

        public BaseBundleItemSubmissionQueryBuilder<M> whereAuthorIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        public BaseBundleItemSubmissionQueryBuilder<M> whereProblemIs(String problemJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.problemJid, problemJid));
            return this;
        }

        public BaseBundleItemSubmissionQueryBuilder<M> whereItemIs(String itemJid) {
            where(columnEq(AbstractBundleItemSubmissionModel_.itemJid, itemJid));
            return this;
        }
    }
}
