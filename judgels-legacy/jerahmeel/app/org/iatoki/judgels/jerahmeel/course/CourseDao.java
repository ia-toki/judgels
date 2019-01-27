package org.iatoki.judgels.jerahmeel.course;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(CourseHibernateDao.class)
public interface CourseDao extends JudgelsDao<CourseModel> {

}
