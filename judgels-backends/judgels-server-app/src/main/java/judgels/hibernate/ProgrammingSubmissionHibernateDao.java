package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.ProgrammingSubmissionDao;
import judgels.persistence.ProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;

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
