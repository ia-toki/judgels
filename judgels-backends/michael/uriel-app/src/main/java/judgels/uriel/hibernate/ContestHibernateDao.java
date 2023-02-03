package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestRoleHibernateDao.isContestantParticipationVisibleAsViewer;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isVisible;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.isVisibleAsViewer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
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
import judgels.persistence.hibernate.OpaqueLiteralExpression;
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
                .addCustomPredicates(isVisible(userJid))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isVisible(userJid))
                .addCustomPredicates(isActive(clock))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllPubliclyParticipatedByUserJid(String userJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isContestantParticipationVisibleAsViewer(userJid))
                .addCustomPredicates(isEnded(clock))
                .build(), options);
    }

    @Override
    public List<ContestModel> selectAllPublicAfter(Instant time) {
        return selectAll(new FilterOptions.Builder<ContestModel>()
                .addCustomPredicates(isVisibleAsViewer())
                .addCustomPredicates(isAfter(time))
                .addCustomPredicates(isEnded(clock))
                .build());
    }

    static CustomPredicateFilter<ContestModel> hasContestJid(String contestJid) {
        return (cb, cq, root) -> cb.equal(root.get(ContestModel_.jid), contestJid);
    }

    static CustomPredicateFilter<ContestModel> isActive(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("timestampadd", Instant.class,
                    new OpaqueLiteralExpression(cb, "second"),
                    cb.quot(root.get(ContestModel_.duration), 1000.0),
                    root.get(ContestModel_.beginTime));

            return cb.greaterThanOrEqualTo(endTime, cb.literal(clock.instant()));
        };
    }

    static CustomPredicateFilter<ContestModel> isEnded(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("timestampadd", Instant.class,
                    new OpaqueLiteralExpression(cb, "second"),
                    cb.quot(root.get(ContestModel_.duration), 1000.0),
                    root.get(ContestModel_.beginTime));

            return cb.lessThan(endTime, cb.literal(clock.instant()));
        };
    }

    static CustomPredicateFilter<ContestModel> isRunning(Clock clock) {
        return (cb, cq, root) -> {
            Instant currentInstant = clock.instant();

            // This is so that the scoreboard updater can update submissions near end time.
            Instant beforeCurrentInstant = currentInstant.minus(Duration.ofSeconds(30));

            Expression<Instant> beginTime = root.get(ContestModel_.beginTime);
            Expression<Instant> endTime = cb.function("timestampadd", Instant.class,
                    new OpaqueLiteralExpression(cb, "second"),
                    cb.quot(root.get(ContestModel_.duration), 1000.0),
                    beginTime);

            return cb.and(
                    cb.greaterThanOrEqualTo(endTime, cb.literal(beforeCurrentInstant)),
                    cb.lessThanOrEqualTo(beginTime, cb.literal(currentInstant)));
        };
    }

    static CustomPredicateFilter<ContestModel> isAfter(Instant time) {
        return (cb, cq, root) -> {
            Expression<Instant> beginTime = root.get(ContestModel_.beginTime);

            return cb.greaterThanOrEqualTo(beginTime, cb.literal(time));
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
