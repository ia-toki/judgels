package org.iatoki.judgels.sandalphon.problem.base;

import com.google.inject.ImplementedBy;
import java.util.List;
import judgels.persistence.UnmodifiableDao;
import judgels.sandalphon.api.problem.ProblemSetterRole;

@ImplementedBy(ProblemSetterHibernateDao.class)
public interface ProblemSetterDao extends UnmodifiableDao<ProblemSetterModel> {
    List<ProblemSetterModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetterModel> selectAllByProblemJidAndRole(String problemJid, ProblemSetterRole role);
}
