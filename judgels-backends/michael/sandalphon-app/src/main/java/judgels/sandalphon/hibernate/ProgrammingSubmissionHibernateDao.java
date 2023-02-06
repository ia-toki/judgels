package judgels.sandalphon.hibernate;

import javax.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.persistence.ProgrammingSubmissionDao;
import judgels.sandalphon.persistence.ProgrammingSubmissionModel;

public class ProgrammingSubmissionHibernateDao
        extends AbstractProgrammingSubmissionHibernateDao<ProgrammingSubmissionModel>
        implements ProgrammingSubmissionDao {

    @Inject
    public ProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingSubmissionModel createSubmissionModel() {
        return new ProgrammingSubmissionModel();
    }
}
