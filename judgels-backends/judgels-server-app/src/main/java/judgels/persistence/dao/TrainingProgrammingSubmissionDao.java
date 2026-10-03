package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.TrainingProgrammingSubmissionModel;
import org.hibernate.query.Query;

public class TrainingProgrammingSubmissionDao
        extends BaseProgrammingSubmissionDao<TrainingProgrammingSubmissionModel> {

    @Inject
    public TrainingProgrammingSubmissionDao(DaoData data) {
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
