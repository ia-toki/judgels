package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ChapterProblemModel;
import judgels.persistence.model.ChapterProblemModel_;
import org.hibernate.Session;

public class ChapterProblemDao extends Dao<ChapterProblemModel> {
    @Inject
    public ChapterProblemDao(DaoData data) {
        super(data);
    }

    public ChapterProblemQueryBuilder selectByChapterJid(String chapterJid) {
        return new ChapterProblemQueryBuilder(currentSession(), chapterJid);
    }

    public ChapterProblemQueryBuilder selectByChapterJids(Collection<String> chapterJids) {
        return new ChapterProblemQueryBuilder(currentSession(), chapterJids);
    }

    public Optional<ChapterProblemModel> selectByProblemJid(String problemJid) {
        return select().where(columnEq(ChapterProblemModel_.problemJid, problemJid)).unique();
    }

    public Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias) {
        return select()
                .where(columnEq(ChapterProblemModel_.chapterJid, chapterJid))
                .where(columnEq(ChapterProblemModel_.alias, problemAlias))
                .unique();
    }

    public List<ChapterProblemModel> selectAllByProblemJids(Collection<String> problemJids) {
        return select().where(columnIn(ChapterProblemModel_.problemJid, problemJids)).all();
    }

    public static class ChapterProblemQueryBuilder extends QueryBuilder<ChapterProblemModel> {
        ChapterProblemQueryBuilder(Session currentSession, String chapterJid) {
            super(currentSession, ChapterProblemModel.class);
            where(columnEq(ChapterProblemModel_.chapterJid, chapterJid));
        }

        ChapterProblemQueryBuilder(Session currentSession, Collection<String> chapterJids) {
            super(currentSession, ChapterProblemModel.class);
            where(columnIn(ChapterProblemModel_.chapterJid, chapterJids));
        }

        public ChapterProblemQueryBuilder whereTypeIs(String type) {
            where(columnEq(ChapterProblemModel_.type, type));
            return this;
        }
    }
}
