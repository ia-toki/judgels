package judgels.uriel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractSubmissionHibernateDao;
import judgels.uriel.persistence.ContestSubmissionDao;
import judgels.uriel.persistence.ContestSubmissionModel;

@Singleton
public class ContestSubmissionHibernateDao extends AbstractSubmissionHibernateDao<ContestSubmissionModel>
        implements ContestSubmissionDao {

    @Inject
    public ContestSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestSubmissionModel createSubmissionModel() {
        return new ContestSubmissionModel();
    }
}
