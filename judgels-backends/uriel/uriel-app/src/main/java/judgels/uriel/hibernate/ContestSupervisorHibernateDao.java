package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.persistence.ContestSupervisorModel_;

@Singleton
public class ContestSupervisorHibernateDao extends HibernateDao<ContestSupervisorModel> implements
        ContestSupervisorDao {

    @Inject
    public ContestSupervisorHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestSupervisorModel> selectByContestJidAndUserJid(String contestJid, String userJid) {
        return selectByFilter(new FilterOptions.Builder<ContestSupervisorModel>()
                .putColumnsEq(ContestSupervisorModel_.contestJid, contestJid)
                .putColumnsEq(ContestSupervisorModel_.userJid, userJid)
                .build());
    }

    @Override
    public Page<ContestSupervisorModel> selectPagedByContestJid(String contestJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestSupervisorModel>()
                .putColumnsEq(ContestSupervisorModel_.contestJid, contestJid)
                .build(), options);
    }

    @Override
    public Set<ContestSupervisorModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestSupervisorModel>()
                .putColumnsEq(ContestSupervisorModel_.contestJid, contestJid)
                .build(), options));
    }

    static CustomPredicateFilter<ContestModel> hasSupervisor(String userJid) {
        return (cb, cq, root) -> {
            Subquery<ContestSupervisorModel> sq = cq.subquery(ContestSupervisorModel.class);
            Root<ContestSupervisorModel> subRoot = sq.from(ContestSupervisorModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestSupervisorModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestSupervisorModel_.userJid), userJid));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
