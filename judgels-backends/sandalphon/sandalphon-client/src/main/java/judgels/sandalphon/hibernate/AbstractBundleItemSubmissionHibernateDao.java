package judgels.sandalphon.hibernate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel;
import judgels.sandalphon.persistence.AbstractBundleItemSubmissionModel_;
import judgels.sandalphon.persistence.BaseBundleItemSubmissionDao;

public abstract class AbstractBundleItemSubmissionHibernateDao<M extends AbstractBundleItemSubmissionModel>
        extends JudgelsHibernateDao<M>
        implements BaseBundleItemSubmissionDao<M> {

    private final Clock clock;

    public AbstractBundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public final Page<M> selectPaged(
            String containerJid,
            Optional<String> createdBy,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options) {

        FilterOptions.Builder<M> filterOptions = new FilterOptions.Builder<>();
        filterOptions.putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid);
        createdBy.ifPresent(jid -> filterOptions.putColumnsEq(JudgelsModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(AbstractBundleItemSubmissionModel_.problemJid, jid));
        lastSubmissionId.ifPresent(filterOptions::lastId);

        return selectPaged(filterOptions.build(), options);
    }

    @Override
    public List<M> selectAllByContainerJid(String containerJid) {
        return selectAll(new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid)
                .build());
    }

    @Override
    public List<M> selectAllByContainerJidAndCreatedBy(
            String containerJid, String createdBy) {
        return selectAll(new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public List<M> selectAllByContainerJidAndProblemJidAndCreatedBy(
            String containerJid, String problemJid, String createdBy) {
        return selectAll(new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public List<M> selectAllByContainerJidAndProblemJidAndCreatedBy(
            String containerJid, Optional<String> problemJid, Optional<String> createdBy) {

        FilterOptions.Builder<M> filterOptions = new FilterOptions.Builder<>();
        filterOptions.putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid);
        createdBy.ifPresent(jid -> filterOptions.putColumnsEq(JudgelsModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(AbstractBundleItemSubmissionModel_.problemJid, jid));

        return selectAll(filterOptions.build());
    }

    @Override
    public Optional<M> selectByContainerJidAndProblemJidAndItemJidAndCreatedBy(
            String containerJid, String problemJid, String itemJid, String createdBy) {
        return selectByFilter(new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractBundleItemSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.problemJid, problemJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.itemJid, itemJid)
                .putColumnsEq(AbstractBundleItemSubmissionModel_.createdBy, createdBy)
                .build());
    }

    @Override
    public M updateGrading(M model, String verdict, Double score) {
        model.verdict = verdict;
        model.score = score;
        model.updatedAt = clock.instant();
        return persist(model);
    }
}
