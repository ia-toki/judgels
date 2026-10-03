package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.TrainingProgrammingGradingModel;
import org.hibernate.query.Query;

public class TrainingProgrammingGradingDao extends BaseProgrammingGradingDao<
        TrainingProgrammingGradingModel> {

    @Inject
    public TrainingProgrammingGradingDao(DaoData data) {
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
