package judgels.uriel.hibernate;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestBundleSubmissionDao;
import judgels.uriel.persistence.ContestBundleSubmissionModel;
import judgels.uriel.persistence.ContestBundleSubmissionModel_;

@Singleton
public class ContestBundleSubmissionHibernateDao extends JudgelsHibernateDao<ContestBundleSubmissionModel> implements
        ContestBundleSubmissionDao {

    @Inject
    public ContestBundleSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndCreatedBy(
            String containerJid, String problemJid, String createdBy) {
        return selectAll(new FilterOptions.Builder<ContestBundleSubmissionModel>()
                .putColumnsEq(ContestBundleSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(ContestBundleSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(ContestBundleSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public Optional<ContestBundleSubmissionModel> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid, String problemJid, String itemJid, String createdBy) {
        return selectByFilter(new FilterOptions.Builder<ContestBundleSubmissionModel>()
                .putColumnsEq(ContestBundleSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(ContestBundleSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(ContestBundleSubmissionModel_.itemJid, itemJid)
                .putColumnsEq(ContestBundleSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public final Page<ContestBundleSubmissionModel> selectPaged(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options) {

        FilterOptions.Builder<ContestBundleSubmissionModel> filterOptions = new FilterOptions.Builder<>();
        filterOptions.putColumnsEq(ContestBundleSubmissionModel_.containerJid, containerJid);
        createdBy.ifPresent(jid -> filterOptions.putColumnsEq(JudgelsModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(ContestBundleSubmissionModel_.problemJid, jid));
        lastSubmissionId.ifPresent(filterOptions::lastId);

        return selectPaged(filterOptions.build(), options);
    }
}
