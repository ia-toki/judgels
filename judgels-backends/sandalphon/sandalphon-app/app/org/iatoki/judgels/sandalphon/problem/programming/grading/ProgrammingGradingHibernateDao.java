package org.iatoki.judgels.sandalphon.problem.programming.grading;

import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingGradingHibernateDao;

public class ProgrammingGradingHibernateDao
        extends AbstractProgrammingGradingHibernateDao<ProgrammingGradingModel>
        implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }

    @Override
    public Class<ProgrammingGradingModel> getGradingModelClass() {
        return ProgrammingGradingModel.class;
    }
}
