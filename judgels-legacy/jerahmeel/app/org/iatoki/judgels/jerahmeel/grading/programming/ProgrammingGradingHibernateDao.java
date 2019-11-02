package org.iatoki.judgels.jerahmeel.grading.programming;

import judgels.jerahmeel.persistence.ProgrammingGradingModel;
import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<ProgrammingGradingModel> implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }
}
