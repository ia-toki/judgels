package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(CurriculumHibernateDao.class)
public interface CurriculumDao extends JudgelsDao<CurriculumModel> {

}
