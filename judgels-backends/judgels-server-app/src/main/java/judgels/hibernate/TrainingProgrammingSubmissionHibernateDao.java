package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.TrainingProgrammingSubmissionDao;
import judgels.persistence.TrainingProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

public class TrainingProgrammingSubmissionHibernateDao
        extends AbstractProgrammingSubmissionHibernateDao<TrainingProgrammingSubmissionModel>
        implements TrainingProgrammingSubmissionDao {

    @Inject
    public TrainingProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public TrainingProgrammingSubmissionModel createSubmissionModel() {
        return new TrainingProgrammingSubmissionModel();
    }

    @Override
    public void updateContainerJid(String problemJid, String containerJid) {
        Query<?> query = currentSession().createQuery(
                "UPDATE jerahmeel_programming_submission  "
                        + "SET containerJid = :containerJid "
                        + "WHERE problemJid = :problemJid");

        query.setParameter("containerJid", containerJid);
        query.setParameter("problemJid", problemJid);
        query.executeUpdate();
    }

    @Override
    public void deleteAllByProblemJid(String problemJid) {
        Query<?> query = currentSession().createQuery(
                "DELETE FROM jerahmeel_programming_submission  "
                        + "WHERE problemJid = :problemJid");

        query.setParameter("problemJid", problemJid);
        query.executeUpdate();
    }
}
