package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.CourseChapterModel;
import judgels.persistence.model.CourseChapterModel_;

public class CourseChapterDao extends Dao<CourseChapterModel> {
    @Inject
    public CourseChapterDao(DaoData data) {
        super(data);
    }

    public Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias) {
        return select()
                .where(columnEq(CourseChapterModel_.courseJid, courseJid))
                .where(columnEq(CourseChapterModel_.alias, chapterAlias))
                .unique();
    }

    public Optional<CourseChapterModel> selectByChapterJid(String chapterJid) {
        return select().where(columnEq(CourseChapterModel_.chapterJid, chapterJid)).unique();
    }

    public QueryBuilder<CourseChapterModel> selectByCourseJid(String courseJid) {
        return select().where(columnEq(CourseChapterModel_.courseJid, courseJid));
    }

    public List<CourseChapterModel> selectAllByChapterJids(Collection<String> chapterJids) {
        return select().where(columnIn(CourseChapterModel_.chapterJid, chapterJids)).all();
    }
}
