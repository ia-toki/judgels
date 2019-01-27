package org.iatoki.judgels.sandalphon.grader;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(GraderHibernateDao.class)
public interface GraderDao extends JudgelsDao<GraderModel> {

}
