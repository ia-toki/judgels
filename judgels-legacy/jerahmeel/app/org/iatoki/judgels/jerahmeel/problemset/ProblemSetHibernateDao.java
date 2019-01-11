package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class ProblemSetHibernateDao extends AbstractJudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {

    public ProblemSetHibernateDao() {
        super(ProblemSetModel.class);
    }
}
