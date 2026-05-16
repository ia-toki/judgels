package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.TrainingProgrammingGradingDao;
import judgels.persistence.TrainingProgrammingGradingModel;
import judgels.persistence.hibernate.HibernateDaoData;
import org.hibernate.query.Query;

public class TrainingProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<
        TrainingProgrammingGradingModel> implements TrainingProgrammingGradingDao {

    @Inject
    public TrainingProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public TrainingProgrammingGradingModel createGradingModel() {
        return new TrainingProgrammingGradingModel();
    }

    @Override
    public Class<TrainingProgrammingGradingModel> getGradingModelClass() {
        return TrainingProgrammingGradingModel.class;
    }

    @Override
    public void deleteAllByProblemJid(String problemJid) {
        Query<?> query = currentSession().createQuery(
                "DELETE FROM jerahmeel_programming_grading "
                        + "WHERE submissionJid IN ("
                        + "SELECT jid FROM jerahmeel_programming_submission WHERE problemJid = :problemJid) ");

        query.setParameter("problemJid", problemJid);
        query.executeUpdate();
    }
}
