package judgels.jerahmeel.hibernate;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ChapterProblemModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import org.hibernate.Session;

public class ChapterProblemHibernateDao extends HibernateDao<ChapterProblemModel> implements ChapterProblemDao {
    @Inject
    public ChapterProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ChapterProblemQueryBuilder selectByChapterJid(String chapterJid) {
        return new ChapterProblemHibernateQueryBuilder(currentSession(), chapterJid);
    }

    @Override
    public ChapterProblemQueryBuilder selectByChapterJids(Collection<String> chapterJids) {
        return new ChapterProblemHibernateQueryBuilder(currentSession(), chapterJids);
    }

    @Override
    public Optional<ChapterProblemModel> selectByProblemJid(String problemJid) {
        return select().where(columnEq(ChapterProblemModel_.problemJid, problemJid)).unique();
    }

    @Override
    public Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias) {
        return select()
                .where(columnEq(ChapterProblemModel_.chapterJid, chapterJid))
                .where(columnEq(ChapterProblemModel_.alias, problemAlias))
                .unique();
    }

    @Override
    public List<ChapterProblemModel> selectAllByProblemJids(Collection<String> problemJids) {
        return select().where(columnIn(ChapterProblemModel_.problemJid, problemJids)).all();
    }

    private static class ChapterProblemHibernateQueryBuilder extends HibernateQueryBuilder<ChapterProblemModel> implements ChapterProblemQueryBuilder {
        ChapterProblemHibernateQueryBuilder(Session currentSession, String chapterJid) {
            super(currentSession, ChapterProblemModel.class);
            where(columnEq(ChapterProblemModel_.chapterJid, chapterJid));
        }

        ChapterProblemHibernateQueryBuilder(Session currentSession, Collection<String> chapterJids) {
            super(currentSession, ChapterProblemModel.class);
            where(columnIn(ChapterProblemModel_.chapterJid, chapterJids));
        }

        @Override
        public ChapterProblemQueryBuilder whereTypeIs(String type) {
            where(columnEq(ChapterProblemModel_.type, type));
            return this;
        }
    }
}
