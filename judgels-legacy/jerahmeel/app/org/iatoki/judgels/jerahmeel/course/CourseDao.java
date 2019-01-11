package org.iatoki.judgels.jerahmeel.course;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(CourseHibernateDao.class)
public interface CourseDao extends JudgelsDao<CourseModel> {

}
