package judgels.persistence;

import java.util.Optional;

public interface CourseDao extends JudgelsDao<CourseModel> {
    Optional<CourseModel> selectBySlug(String courseSlug);
}
