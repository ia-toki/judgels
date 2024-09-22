package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingSubmissionHibernateDao;
import org.hibernate.query.Query;

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

    @Override
    public void updateContainer(String problemJid, String containerJid) {
        Query query = currentSession().createQuery(
                "UPDATE jerahmeel_programming_submission  "
                        + "SET containerJid = :containerJid "
                        + "WHERE problemJid = :problemJid");

        query.setParameter("containerJid", containerJid);
        query.setParameter("problemJid", problemJid);
        query.executeUpdate();
    }
}
