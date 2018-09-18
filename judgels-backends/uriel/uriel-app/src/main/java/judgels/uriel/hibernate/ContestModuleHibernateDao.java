package judgels.uriel.hibernate;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestModuleModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestModuleHibernateDao extends HibernateDao<ContestModuleModel> implements ContestModuleDao {
    @Inject
    public ContestModuleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
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
