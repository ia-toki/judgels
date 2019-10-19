package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface CourseDao extends JudgelsDao<CourseModel> {
    Optional<CourseModel> selectBySlug(String courseSlug);
}
