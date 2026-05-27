package judgels.persistence.dao;

import judgels.persistence.QueryBuilder;
import judgels.persistence.model.AbstractBundleItemSubmissionModel;

public interface BaseBundleItemSubmissionDao<M extends AbstractBundleItemSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();

    BaseBundleItemSubmissionQueryBuilder<M> select();
    void deleteAllByProblemJid(String problemJid);

    interface BaseBundleItemSubmissionQueryBuilder<M extends AbstractBundleItemSubmissionModel> extends QueryBuilder<M> {
        BaseBundleItemSubmissionQueryBuilder<M> whereContainerIs(String containerJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereAuthorIs(String userJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereProblemIs(String problemJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereItemIs(String itemJid);
    }
}
