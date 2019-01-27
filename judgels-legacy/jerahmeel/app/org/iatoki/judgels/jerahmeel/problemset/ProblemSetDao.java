package org.iatoki.judgels.jerahmeel.problemset;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ProblemSetHibernateDao.class)
public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {

}
