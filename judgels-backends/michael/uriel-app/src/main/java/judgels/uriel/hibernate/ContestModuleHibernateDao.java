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
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestModuleModel_;

@Singleton
public class ContestModuleHibernateDao extends HibernateDao<ContestModuleModel> implements ContestModuleDao {
    @Inject
    public ContestModuleHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ContestModuleModel> selectByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByFilter(new FilterOptions.Builder<ContestModuleModel>()
                .putColumnsEq(ContestModuleModel_.contestJid, contestJid)
                .putColumnsEq(ContestModuleModel_.name, type.name())
                .build());
    }

    @Override
    public Optional<ContestModuleModel> selectEnabledByContestJidAndType(String contestJid, ContestModuleType type) {
        return selectByFilter(new FilterOptions.Builder<ContestModuleModel>()
                .putColumnsEq(ContestModuleModel_.contestJid, contestJid)
                .putColumnsEq(ContestModuleModel_.name, type.name())
                .putColumnsEq(ContestModuleModel_.enabled, true)
                .build());
    }

    @Override
    public Set<ContestModuleModel> selectAllByContestJid(String contestJid, SelectionOptions options) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestModuleModel>()
                .putColumnsEq(ContestModuleModel_.contestJid, contestJid)
                .build(), options));
    }

    @Override
    public Set<ContestModuleModel> selectAllEnabledByContestJid(String contestJid) {
        return ImmutableSet.copyOf(selectAll(new FilterOptions.Builder<ContestModuleModel>()
                .putColumnsEq(ContestModuleModel_.contestJid, contestJid)
                .putColumnsEq(ContestModuleModel_.enabled, true)
                .build()));
    }

    static CustomPredicateFilter<ContestModel> hasModule(ContestModuleType type) {
        return (cb, cq, root) -> {
            Subquery<ContestModuleModel> sq = cq.subquery(ContestModuleModel.class);
            Root<ContestModuleModel> subRoot = sq.from(ContestModuleModel.class);

            sq.where(
                    cb.equal(subRoot.get(ContestModuleModel_.contestJid), root.get(ContestModel_.jid)),
                    cb.equal(subRoot.get(ContestModuleModel_.name), type.name()),
                    cb.isTrue(subRoot.get(ContestModuleModel_.enabled)));
            sq.select(subRoot);

            return cb.exists(sq);
        };
    }
}
