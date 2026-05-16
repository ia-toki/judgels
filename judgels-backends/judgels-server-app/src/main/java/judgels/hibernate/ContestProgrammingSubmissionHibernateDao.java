package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.persistence.ContestProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

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

    @Override
    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE uriel_contest_programming_submission "
                        + "SET problemJid = :newProblemJid "
                        + "WHERE problemJid = :oldProblemJid");

        query.setParameter("newProblemJid", newProblemJid);
        query.setParameter("oldProblemJid", oldProblemJid);
        query.executeUpdate();
    }
}
