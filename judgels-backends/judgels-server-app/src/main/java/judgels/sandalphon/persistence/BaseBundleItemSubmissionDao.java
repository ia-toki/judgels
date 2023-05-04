package judgels.sandalphon.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface BaseBundleItemSubmissionDao<M extends AbstractBundleItemSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();

    BaseBundleItemSubmissionQueryBuilder<M> select();

    interface BaseBundleItemSubmissionQueryBuilder<M extends AbstractBundleItemSubmissionModel> extends QueryBuilder<M> {
        BaseBundleItemSubmissionQueryBuilder<M> whereContainerIs(String containerJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereAuthorIs(String userJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereProblemIs(String problemJid);
        BaseBundleItemSubmissionQueryBuilder<M> whereItemIs(String itemJid);
    }
}
