package judgels.persistence.dao;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.AbstractProgrammingSubmissionModel;
import judgels.persistence.model.AbstractProgrammingSubmissionModel_;
import org.hibernate.Session;

public abstract class BaseProgrammingSubmissionDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsDao<M> {
    public BaseProgrammingSubmissionDao(DaoData data) {
        super(data);
    }

    public abstract M createSubmissionModel();

    @Override
    public BaseProgrammingSubmissionQueryBuilder<M> select() {
        return new BaseProgrammingSubmissionQueryBuilder<>(currentSession(), getEntityClass());
    }

    public Map<String, Long> selectCounts(String containerJid, String userJid, Collection<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<M> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(AbstractProgrammingSubmissionModel_.problemJid),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid),
                cb.equal(root.get(JudgelsModel_.createdBy), userJid),
                root.get(AbstractProgrammingSubmissionModel_.problemJid).in(problemJids));

        cq.groupBy(
                root.get(AbstractProgrammingSubmissionModel_.containerJid),
                root.get(JudgelsModel_.createdBy),
                root.get(AbstractProgrammingSubmissionModel_.problemJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }

    public void updateContainerJid(String problemJid, String containerJid) {
        throw new UnsupportedOperationException();
    }

    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        throw new UnsupportedOperationException();
    }

    public void deleteAllByProblemJid(String problemJid) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> dump(PrintWriter output, String containerJid) {
        List<M> results = select().whereContainerIs(containerJid).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return List.of();
        }

        output.write("INSERT IGNORE INTO uriel_contest_programming_submission (jid, problemJid, containerJid, gradingEngine, gradingLanguage, createdBy, createdAt, updatedBy, updatedAt) VALUES\n");

        List<String> submissionJids = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            M m = results.get(i);
            if (i > 0) {
                output.write(",\n");
            }
            output.write(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    escape(m.jid),
                    escape(m.problemJid),
                    escape(m.containerJid),
                    escape(m.gradingEngine),
                    escape(m.gradingLanguage),
                    escape(m.createdBy),
                    escape(m.createdAt),
                    escape(m.updatedBy),
                    escape(m.updatedAt)));
            submissionJids.add(m.jid);
        }
        output.write(";\n");
        return submissionJids;
    }

    public static class BaseProgrammingSubmissionQueryBuilder<M extends AbstractProgrammingSubmissionModel> extends QueryBuilder<M> {
        BaseProgrammingSubmissionQueryBuilder(Session currentSession, Class<M> entityClass) {
            super(currentSession, entityClass);
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereContainerIs(String containerJid) {
            where(columnEq(AbstractProgrammingSubmissionModel_.containerJid, containerJid));
            return this;
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereAuthorIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereProblemIs(String problemJid) {
            where(columnEq(AbstractProgrammingSubmissionModel_.problemJid, problemJid));
            return this;
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereLastSubmissionIs(long submissionId) {
            where((cb, cq, root) -> cb.gt(root.get(UnmodifiableModel_.id), submissionId));
            return this;
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereIdLessThan(long id) {
            where((cb, cq, root) -> cb.lt(root.get(UnmodifiableModel_.id), id));
            return this;
        }

        public BaseProgrammingSubmissionQueryBuilder<M> whereIdGreaterThan(long id) {
            where((cb, cq, root) -> cb.gt(root.get(UnmodifiableModel_.id), id));
            return this;
        }
    }
}
