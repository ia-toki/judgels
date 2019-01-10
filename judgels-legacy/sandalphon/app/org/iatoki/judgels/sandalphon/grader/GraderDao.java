package org.iatoki.judgels.sandalphon.grader;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(GraderHibernateDao.class)
public interface GraderDao extends JudgelsDao<GraderModel> {

}
