package org.iatoki.judgels.jerahmeel.problemset;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ProblemSetHibernateDao.class)
public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {

}
