package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingSubmissionHibernateDao;

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
