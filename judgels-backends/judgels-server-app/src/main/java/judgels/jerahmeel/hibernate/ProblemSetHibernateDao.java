package judgels.jerahmeel.hibernate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetModel_;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;

public class ProblemSetHibernateDao extends JudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {
    @Inject
    public ProblemSetHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProblemSetHibernateQueryBuilder select() {
        return new ProblemSetHibernateQueryBuilder(currentSession());
    }

    @Override
    public Optional<ProblemSetModel> selectBySlug(String problemSetSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return select()
                .where((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ProblemSetModel_.slug), problemSetSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(problemSetSlug, 0))))
                .unique();
    }

    @Override
    public List<ProblemSetModel> selectAllBySlugs(Set<String> contestSlugs) {
        return select().where(columnIn(ProblemSetModel_.slug, contestSlugs)).all();
    }

    private static class ProblemSetHibernateQueryBuilder extends HibernateQueryBuilder<ProblemSetModel> implements ProblemSetQueryBuilder {
        ProblemSetHibernateQueryBuilder(Session currentSession) {
            super(currentSession, ProblemSetModel.class);
        }

        @Override
        public ProblemSetQueryBuilder whereArchiveIs(String archiveJid) {
            where(columnEq(ProblemSetModel_.archiveJid, archiveJid));
            return this;
        }

        @Override
        public ProblemSetQueryBuilder whereNameLike(String name) {
            where(columnLike(ProblemSetModel_.name, name));
            return this;
        }
    }
}
