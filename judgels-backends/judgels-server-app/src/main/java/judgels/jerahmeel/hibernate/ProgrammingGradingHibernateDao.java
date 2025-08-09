package judgels.jerahmeel.hibernate;

import jakarta.inject.Inject;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingGradingModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingGradingHibernateDao;
import org.hibernate.query.Query;

public class ProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<
        ProgrammingGradingModel> implements ProgrammingGradingDao {

    @Inject
    public ProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ProgrammingGradingModel createGradingModel() {
        return new ProgrammingGradingModel();
    }

    @Override
    public Class<ProgrammingGradingModel> getGradingModelClass() {
        return ProgrammingGradingModel.class;
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
