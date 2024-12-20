package judgels.uriel.hibernate;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractProgrammingGradingHibernateDao;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel_;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingGradingModel;

public class ContestProgrammingGradingHibernateDao extends AbstractProgrammingGradingHibernateDao<
        ContestProgrammingGradingModel> implements ContestProgrammingGradingDao {

    @Inject
    public ContestProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestProgrammingGradingModel createGradingModel() {
        return new ContestProgrammingGradingModel();
    }

    @Override
    public Class<ContestProgrammingGradingModel> getGradingModelClass() {
        return ContestProgrammingGradingModel.class;
    }

    @Override
    public void dump(PrintWriter output, Collection<String> submissionJids) {
        List<ContestProgrammingGradingModel> results = select().where(columnIn(AbstractProgrammingGradingModel_.submissionJid, submissionJids)).orderBy(
                Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_programming_grading (jid, submissionJid, verdictCode, verdictName, score, details, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            ContestProgrammingGradingModel m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.jid),
                    escape(m.submissionJid),
                    escape(m.verdictCode),
                    escape(""),
                    escape(m.score),
                    escape(m.details),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedAt)));
        }
        output.write(";\n");
    }
}
