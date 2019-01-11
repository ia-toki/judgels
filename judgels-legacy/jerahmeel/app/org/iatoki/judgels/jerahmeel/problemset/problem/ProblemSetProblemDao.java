package org.iatoki.judgels.jerahmeel.problemset.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ProblemSetProblemHibernateDao.class)
public interface ProblemSetProblemDao extends Dao<Long, ProblemSetProblemModel> {

    boolean existsByProblemSetJidAndAlias(String problemSetJid, String alias);

    List<ProblemSetProblemModel> getByProblemSetJid(String problemSetJid);

    ProblemSetProblemModel findByProblemSetJidAndProblemJid(String problemSetJid, String problemJid);
}
