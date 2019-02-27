package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingSubmissionHibernateDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;

@Singleton
public class ContestProgrammingSubmissionHibernateDao
        extends AbstractProgrammingSubmissionHibernateDao<ContestProgrammingSubmissionModel>
        implements ContestProgrammingSubmissionDao {

    @Inject
    public ContestProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestProgrammingSubmissionModel createSubmissionModel() {
        return new ContestProgrammingSubmissionModel();
    }
}
