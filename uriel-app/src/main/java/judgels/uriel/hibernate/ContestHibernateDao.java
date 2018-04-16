package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestRoleHibernateDao.isContestantPredicate;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isManagerPredicate;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isSupervisorPredicate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
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
    public Page<ContestModel> selectAllByUserJid(Optional<String> userJid, SelectionOptions options) {
        FilterOptions.Builder<ContestModel> filterOptions = new FilterOptions.Builder<>();
        userJid.ifPresent(jid ->
                filterOptions.addCustomPredicates((cb, cq, root) -> isVisiblePredicate(cb, cq, root, jid)));

        return selectAll(filterOptions.build(), options);
    }

    @Override
    public List<ContestModel> selectAllActiveByUserJid(Optional<String> userJid, SelectionOptions options) {
        FilterOptions.Builder<ContestModel> filterOptions = new FilterOptions.Builder<>();
        filterOptions.addCustomPredicates(this::isActivePredicate);
        userJid.ifPresent(jid ->
                filterOptions.addCustomPredicates((cb, cq, root) -> isVisiblePredicate(cb, cq, root, jid)));

        return selectAll(filterOptions.build(), options).getData();
    }

    @Override
    public Page<ContestModel> selectAllPastByUserJid(Optional<String> userJid, SelectionOptions options) {
        FilterOptions.Builder<ContestModel> filterOptions = new FilterOptions.Builder<>();
        filterOptions.addCustomPredicates(this::isPastPredicate);
        userJid.ifPresent(jid ->
                filterOptions.addCustomPredicates((cb, cq, root) -> isVisiblePredicate(cb, cq, root, jid)));

        return selectAll(filterOptions.build(), options);
    }

    private Predicate isVisiblePredicate(
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<ContestModel> root,
            String userJid) {

        return cb.or(
                isContestantPredicate(cb, cq, root, userJid),
                isSupervisorPredicate(cb, cq, root, userJid),
                isManagerPredicate(cb, cq, root, userJid));
    }

    // The following two predicate is currently not testable because H2 does not have 'unix_timestamp' function.
    private Predicate isActivePredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<ContestModel> root) {
        long currentInstantEpoch = clock.instant().toEpochMilli();
        Expression<Long> beginTime = cb.prod(
                cb.function("unix_timestamp", Double.class, root.get(ContestModel_.beginTime)),
                cb.literal(1000.0)).as(Long.class);
        Expression<Long> endTime = cb.sum(beginTime, root.get(ContestModel_.duration));

        return cb.greaterThanOrEqualTo(endTime, cb.literal(currentInstantEpoch));
    }

    private Predicate isPastPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<ContestModel> root) {
        return cb.not(isActivePredicate(cb, cq, root));
    }
}
