package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.model.CourseModel;

public interface CourseDao extends JudgelsDao<CourseModel> {
    Optional<CourseModel> selectBySlug(String courseSlug);
}
