package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.CourseModel;
import judgels.persistence.model.CourseModel_;

public class CourseDao extends JudgelsDao<CourseModel> {
    @Inject
    public CourseDao(DaoData data) {
        super(data);
    }

    public Optional<CourseModel> selectBySlug(String courseSlug) {
        return select().where(columnEq(CourseModel_.slug, courseSlug)).unique();
    }
}
