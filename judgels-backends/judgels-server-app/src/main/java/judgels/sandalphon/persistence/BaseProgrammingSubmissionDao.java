package judgels.sandalphon.persistence;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface BaseProgrammingSubmissionDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();
    BaseProgrammingSubmissionQueryBuilder<M> select();
    Map<String, Long> selectCounts(String containerJid, String userJid, Collection<String> problemJids);
    Collection<String> dump(PrintWriter output, String containerJid);

    interface BaseProgrammingSubmissionQueryBuilder<M extends AbstractProgrammingSubmissionModel> extends QueryBuilder<M> {
        BaseProgrammingSubmissionQueryBuilder<M> whereContainerIs(String containerJid);
        BaseProgrammingSubmissionQueryBuilder<M> whereAuthorIs(String userJid);
        BaseProgrammingSubmissionQueryBuilder<M> whereProblemIs(String problemJid);
        BaseProgrammingSubmissionQueryBuilder<M> whereLastSubmissionIs(long submissionId);
    }
}
