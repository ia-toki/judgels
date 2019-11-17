package org.iatoki.judgels.jerahmeel.problemset.problem;

import com.google.inject.ImplementedBy;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.persistence.Dao;

import java.util.List;

@ImplementedBy(ProblemSetProblemHibernateDao.class)
public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {

    boolean existsByProblemSetJidAndAlias(String problemSetJid, String alias);

    List<ProblemSetProblemModel> getByProblemSetJid(String problemSetJid);

    ProblemSetProblemModel findByProblemSetJidAndProblemJid(String problemSetJid, String problemJid);
}
