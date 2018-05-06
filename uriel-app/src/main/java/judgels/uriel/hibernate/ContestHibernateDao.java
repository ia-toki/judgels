package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.not;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.hasViewer;

import java.time.Clock;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Expression;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    private final Clock clock;

    @Inject
    public ContestHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
        this.clock = clock;
    }


    @Override
    public List<ContestModel> selectAllActive(SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isActive(clock))
                .build(), options);
    }

    @Override
    public Page<ContestModel> selectPagedPast(SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isPast(clock))
                .build(), options);
    }

    @Override
    public Page<ContestModel> selectPagedByUserJid(String userJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasViewer(userJid))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasViewer(userJid))
                .addCustomPredicates(isActive(clock))
                .build(), options);
    }

    @Override
    public Page<ContestModel> selectPagedPastByUserJid(String userJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasViewer(userJid))
                .addCustomPredicates(isPast(clock))
                .build(), options);
    }

    static CustomPredicateFilter<ContestModel> hasContestJid(String contestJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid);
    }

    // The following two predicates are currently not testable because H2 does not have 'unix_timestamp' function.
    static CustomPredicateFilter<ContestModel> isActive(Clock clock) {
        return (cb, cq, root) -> {
            long currentInstantEpoch = clock.instant().toEpochMilli();
            Expression<Long> beginTime = cb.prod(
                    cb.function("unix_timestamp", Double.class, root.get(ContestModel_.beginTime)),
                    cb.literal(1000.0)).as(Long.class);
            Expression<Long> endTime = cb.sum(beginTime, root.get(ContestModel_.duration));

            return cb.greaterThanOrEqualTo(endTime, cb.literal(currentInstantEpoch));
        };
    }

    static CustomPredicateFilter<ContestModel> isPast(Clock clock) {
        return not(isActive(clock));
    }
}
