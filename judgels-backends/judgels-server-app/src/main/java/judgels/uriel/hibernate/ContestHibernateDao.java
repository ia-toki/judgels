package judgels.uriel.hibernate;

import static judgels.uriel.hibernate.ContestRoleHibernateDao.isPublic;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.userCanView;
import static judgels.uriel.hibernate.ContestRoleHibernateDao.userParticipated;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.criteria.Expression;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.persistence.hibernate.OpaqueLiteralExpression;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;

public class ContestHibernateDao extends JudgelsHibernateDao<ContestModel> implements ContestDao {
    private final Clock clock;

    @Inject
    public ContestHibernateDao(HibernateDaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public ContestHibernateQueryBuilder select() {
        return new ContestHibernateQueryBuilder(currentSession(), clock);
    }

    @Override
    public Optional<ContestModel> selectBySlug(String contestSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return select()
                .where((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ContestModel_.slug), contestSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(contestSlug, 0))))
                .unique();
    }

    @Override
    public List<ContestModel> selectAllBySlugs(Set<String> contestSlugs) {
        return select().where(columnIn(ContestModel_.slug, contestSlugs)).all();
    }

    private static class ContestHibernateQueryBuilder extends HibernateQueryBuilder<ContestModel> implements ContestQueryBuilder {
        private final Clock clock;

        ContestHibernateQueryBuilder(Session currentSession, Clock clock) {
            super(currentSession, ContestModel.class);
            this.clock = clock;
        }

        @Override
        public ContestQueryBuilder wherePublic() {
            where(isPublic());
            return this;
        }

        @Override
        public ContestQueryBuilder whereActive() {
            where(isActive(clock));
            return this;
        }

        @Override
        public ContestQueryBuilder whereRunning() {
            where(isRunning(clock));
            return this;
        }

        @Override
        public ContestQueryBuilder whereBeginsAfter(Instant time) {
            where(beginsAfter(time));
            return this;
        }

        @Override
        public ContestQueryBuilder whereEnded() {
            where(isEnded(clock));
            return this;
        }

        @Override
        public ContestQueryBuilder whereUserCanView(String userJid) {
            where(userCanView(userJid));
            return this;
        }

        @Override
        public ContestQueryBuilder whereUserParticipated(String userJid) {
            where(userParticipated(userJid));
            return this;
        }

        @Override
        public ContestQueryBuilder whereNameLike(String name) {
            where(columnEq(ContestModel_.name, name));
            return this;
        }
    }

    static CriteriaPredicate<ContestModel> isActive(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("timestampadd", Instant.class,
                    new OpaqueLiteralExpression(cb, "second"),
                    cb.quot(root.get(ContestModel_.duration), 1000.0),
                    root.get(ContestModel_.beginTime));

            return cb.greaterThanOrEqualTo(endTime, cb.literal(clock.instant()));
        };
    }

    static CriteriaPredicate<ContestModel> isEnded(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("timestampadd", Instant.class,
                    new OpaqueLiteralExpression(cb, "second"),
                    cb.quot(root.get(ContestModel_.duration), 1000.0),
                    root.get(ContestModel_.beginTime));

            return cb.lessThan(endTime, cb.literal(clock.instant()));
        };
    }

    static CriteriaPredicate<ContestModel> isRunning(Clock clock) {
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

    static CriteriaPredicate<ContestModel> beginsAfter(Instant time) {
        return (cb, cq, root) -> {
            Expression<Instant> beginTime = root.get(ContestModel_.beginTime);

            return cb.greaterThanOrEqualTo(beginTime, cb.literal(time));
        };
    }
}
