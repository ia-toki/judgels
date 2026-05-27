package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.ProgrammingSubmissionDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.ProgrammingSubmissionModel;

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
