package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestRoleHibernateDao.hasViewerOrAbove;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.Expression;
import judgels.persistence.CustomPredicateFilter;
import judgels.persistence.FilterOptions;
import judgels.persistence.Model_;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.apache.commons.lang3.math.NumberUtils;

@Singleton
public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    private final Clock clock;

    @Inject
    public ContestHibernateDao(HibernateDaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public Optional<ContestModel> selectBySlug(String contestSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return selectByFilter(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ContestModel_.slug), contestSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(contestSlug, 0))))
                .build());
    }

    @Override
    public List<ContestModel> selectAllActive(SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isActive(clock))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllRunning(SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isRunning(clock))
                .build(), options);
    }

    @Override
    public Page<ContestModel> selectPaged(SearchOptions searchOptions, SelectionOptions options) {
        return selectPaged(createFilterOptions(searchOptions).build(), options);
    }

    @Override
    public Page<ContestModel> selectPagedByUserJid(
            String userJid,
            SearchOptions searchOptions,
            SelectionOptions options) {

        return selectPaged(createFilterOptions(searchOptions)
                .addCustomPredicates(hasViewerOrAbove(userJid))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(hasViewerOrAbove(userJid))
                .addCustomPredicates(isActive(clock))
                .build(), options);
    }

    static CustomPredicateFilter<ContestModel> hasContestJid(String contestJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid);
    }

    // The following predicate is currently not testable because H2 does not have 'unix_timestamp' function.
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

    // The following predicate is currently not testable because H2 does not have 'unix_timestamp' function.
    static CustomPredicateFilter<ContestModel> isRunning(Clock clock) {
        return (cb, cq, root) -> {
            long currentInstantEpoch = clock.instant().toEpochMilli();
            Expression<Long> beginTime = cb.prod(
                    cb.function("unix_timestamp", Double.class, root.get(ContestModel_.beginTime)),
                    cb.literal(1000.0)).as(Long.class);
            Expression<Long> endTime = cb.sum(beginTime, root.get(ContestModel_.duration));

            return cb.and(
                    cb.greaterThanOrEqualTo(endTime, cb.literal(currentInstantEpoch)),
                    cb.lessThanOrEqualTo(beginTime, cb.literal(currentInstantEpoch)));
        };
    }

    private static FilterOptions.Builder<ContestModel> createFilterOptions(SearchOptions searchOptions) {
        FilterOptions.Builder<ContestModel> filterOptions = new FilterOptions.Builder<>();

        if (searchOptions.getTerms().containsKey("name")) {
            filterOptions.putColumnsLike(ContestModel_.name, searchOptions.getTerms().get("name"));
        }

        return filterOptions;
    }
}
