package judgels.persistence.dao;

import static judgels.persistence.dao.ContestRoleDao.isPublic;
import static judgels.persistence.dao.ContestRoleDao.userCanView;
import static judgels.persistence.dao.ContestRoleDao.userParticipated;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.Expression;
import java.io.PrintWriter;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestModel;
import judgels.persistence.model.ContestModel_;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;

public class ContestDao extends JudgelsDao<ContestModel> {
    private final Clock clock;

    @Inject
    public ContestDao(DaoData data) {
        super(data);
        this.clock = data.getClock();
    }

    @Override
    public ContestQueryBuilder select() {
        return new ContestQueryBuilder(currentSession(), clock);
    }

    public Optional<ContestModel> selectBySlug(String contestSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return select()
                .where((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ContestModel_.slug), contestSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(contestSlug, 0))))
                .unique();
    }

    public List<ContestModel> selectAllBySlugs(Collection<String> contestSlugs) {
        return select().where(columnIn(ContestModel_.slug, contestSlugs)).all();
    }

    public void dump(PrintWriter output, String contestJid) {
        Optional<ContestModel> maybeModel = selectByJid(contestJid);
        if (maybeModel.isEmpty()) {
            return;
        }

        ContestModel m = maybeModel.get();

        output.write("INSERT IGNORE INTO uriel_contest (jid, slug, name, description, style, beginTime, duration, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");
        output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);\n",
                escape(m.jid),
                escape(m.slug),
                escape(m.name),
                escape(m.description),
                escape(m.style),
                escape(m.beginTime),
                escape(m.duration),
                escape(m.createdBy),
                escape(m.createdAt),
                escape(m.updatedBy),
                escape(m.updatedAt)));
    }

    public static class ContestQueryBuilder extends QueryBuilder<ContestModel> {
        private final Clock clock;

        ContestQueryBuilder(Session currentSession, Clock clock) {
            super(currentSession, ContestModel.class);
            this.clock = clock;
        }

        public ContestQueryBuilder wherePublic() {
            where(isPublic());
            return this;
        }

        public ContestQueryBuilder whereActive() {
            where(isActive(clock));
            return this;
        }

        public ContestQueryBuilder whereRunning() {
            where(isRunning(clock));
            return this;
        }

        public ContestQueryBuilder whereBeginsAfter(Instant time) {
            where(beginsAfter(time));
            return this;
        }

        public ContestQueryBuilder whereEnded() {
            where(isEnded(clock));
            return this;
        }

        public ContestQueryBuilder whereUserCanView(String userJid) {
            where(userCanView(userJid));
            return this;
        }

        public ContestQueryBuilder whereUserParticipated(String userJid) {
            where(userParticipated(userJid));
            return this;
        }

        public ContestQueryBuilder whereNameLike(String name) {
            where(columnLike(ContestModel_.name, name));
            return this;
        }
    }

    static CriteriaPredicate<ContestModel> isActive(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("from_unixtime", Instant.class, cb.sum(
                    cb.function("unix_timestamp", Long.class, root.get(ContestModel_.beginTime)),
                    cb.quot(root.get(ContestModel_.duration), 1000L)));

            return cb.greaterThanOrEqualTo(endTime, cb.literal(clock.instant()));
        };
    }

    static CriteriaPredicate<ContestModel> isEnded(Clock clock) {
        return (cb, cq, root) -> {
            Expression<Instant> endTime = cb.function("from_unixtime", Instant.class, cb.sum(
                    cb.function("unix_timestamp", Long.class, root.get(ContestModel_.beginTime)),
                    cb.quot(root.get(ContestModel_.duration), 1000L)));

            return cb.lessThan(endTime, cb.literal(clock.instant()));
        };
    }

    static CriteriaPredicate<ContestModel> isRunning(Clock clock) {
        return (cb, cq, root) -> {
            Instant currentInstant = clock.instant();

            // This is so that the scoreboard updater can update submissions near end time.
            Instant beforeCurrentInstant = currentInstant.minus(Duration.ofSeconds(30));

            Expression<Instant> beginTime = root.get(ContestModel_.beginTime);
            Expression<Instant> endTime = cb.function("from_unixtime", Instant.class, cb.sum(
                    cb.function("unix_timestamp", Long.class, beginTime),
                    cb.quot(root.get(ContestModel_.duration), 1000L)));

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
