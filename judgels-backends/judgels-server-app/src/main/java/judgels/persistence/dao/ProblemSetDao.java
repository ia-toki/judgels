package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ProblemSetModel;
import judgels.persistence.model.ProblemSetModel_;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;

public class ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    @Inject
    public ProblemSetDao(DaoData data) {
        super(data);
    }

    @Override
    public ProblemSetQueryBuilder select() {
        return new ProblemSetQueryBuilder(currentSession());
    }

    public Optional<ProblemSetModel> selectBySlug(String problemSetSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return select()
                .where((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ProblemSetModel_.slug), problemSetSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(problemSetSlug, 0))))
                .unique();
    }

    public List<ProblemSetModel> selectAllBySlugs(Collection<String> contestSlugs) {
        return select().where(columnIn(ProblemSetModel_.slug, contestSlugs)).all();
    }

    public static class ProblemSetQueryBuilder extends QueryBuilder<ProblemSetModel> {
        ProblemSetQueryBuilder(Session currentSession) {
            super(currentSession, ProblemSetModel.class);
        }

        public ProblemSetQueryBuilder whereArchiveIs(String archiveJid) {
            where(columnEq(ProblemSetModel_.archiveJid, archiveJid));
            return this;
        }

        public ProblemSetQueryBuilder whereNameLike(String name) {
            where(columnLike(ProblemSetModel_.name, name));
            return this;
        }
    }
}
