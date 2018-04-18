package judgels.uriel.hibernate;

import static judgels.persistence.CustomPredicateFilter.literalTrue;
import static judgels.persistence.CustomPredicateFilter.not;
import static judgels.persistence.CustomPredicateFilter.or;
import static judgels.uriel.hibernate.ContestModuleHibernateDao.hasModule;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.hasContestant;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.hasManager;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.hasSupervisor;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Expression;
import judgels.persistence.ActorProvider;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.module.ContestModule;
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
    public Page<ContestModel> selectAllByUserJid(Optional<String> userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isVisibleTo(userJid))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllActiveByUserJid(Optional<String> userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isVisibleTo(userJid))
                .addCustomPredicates(isActive())
                .build(), options).getData();
    }

    @Override
    public Page<ContestModel> selectAllPastByUserJid(Optional<String> userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isVisibleTo(userJid))
                .addCustomPredicates(isPast())
                .build(), options);
    }

    static CustomPredicateFilter<ContestModel> hasContestJid(String contestJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid);
    }

    private static CustomPredicateFilter<ContestModel> isVisibleTo(Optional<String> userJid) {
        return userJid
                .map(jid -> or(
                        hasModule(ContestModule.REGISTRATION),
                        hasContestant(jid),
                        hasSupervisor(jid),
                        hasManager(jid)))
                .orElse(literalTrue());
    }

    // The following two predicates are currently not testable because H2 does not have 'unix_timestamp' function.
    private CustomPredicateFilter<ContestModel> isActive() {
        return (cb, cq, root) -> {
            long currentInstantEpoch = clock.instant().toEpochMilli();
            Expression<Long> beginTime = cb.prod(
                    cb.function("unix_timestamp", Double.class, root.get(ContestModel_.beginTime)),
                    cb.literal(1000.0)).as(Long.class);
            Expression<Long> endTime = cb.sum(beginTime, root.get(ContestModel_.duration));

            return cb.greaterThanOrEqualTo(endTime, cb.literal(currentInstantEpoch));
        };
    }

    private CustomPredicateFilter<ContestModel> isPast() {
        return not(isActive());
    }
}
