package judgels.uriel.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel_;

@Singleton
public class ContestBundleItemSubmissionHibernateDao extends JudgelsHibernateDao<ContestBundleItemSubmissionModel>
        implements ContestBundleItemSubmissionDao {

    @Inject
    public ContestBundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ContestBundleItemSubmissionModel> selectByContainerJidAndCreatedBy(
            String containerJid, String createdBy) {
        return selectAll(new FilterOptions.Builder<ContestBundleItemSubmissionModel>()
                .putColumnsEq(ContestBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public List<ContestBundleItemSubmissionModel> selectByContainerJidAndProblemJidAndCreatedBy(
            String containerJid, String problemJid, String createdBy) {
        return selectAll(new FilterOptions.Builder<ContestBundleItemSubmissionModel>()
                .putColumnsEq(ContestBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public Optional<ContestBundleItemSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid, String problemJid, String itemJid, String createdBy) {
        return selectByFilter(new FilterOptions.Builder<ContestBundleItemSubmissionModel>()
                .putColumnsEq(ContestBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.itemJid, itemJid)
                .putColumnsEq(ContestBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }
}
