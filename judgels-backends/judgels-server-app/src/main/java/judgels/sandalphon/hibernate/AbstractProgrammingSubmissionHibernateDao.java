package judgels.sandalphon.hibernate;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.HibernateQueryBuilder;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel_;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao;
import org.hibernate.Session;

public abstract class AbstractProgrammingSubmissionHibernateDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsHibernateDao<M> implements BaseProgrammingSubmissionDao<M> {
    public AbstractProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public AbstractProgrammingSubmissionHibernateQueryBuilder<M> select() {
        return new AbstractProgrammingSubmissionHibernateQueryBuilder<>(currentSession(), getEntityClass());
    }

    @Override
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

    @Override
    public void updateContainerJid(String problemJid, String containerJid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateProblemJid(String oldProblemJid, String newProblemJid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllByProblemJid(String problemJid) {
        throw new UnsupportedOperationException();
    }

    @Override
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

    private static class AbstractProgrammingSubmissionHibernateQueryBuilder<M extends AbstractProgrammingSubmissionModel> extends HibernateQueryBuilder<M> implements BaseProgrammingSubmissionQueryBuilder<M> {
        AbstractProgrammingSubmissionHibernateQueryBuilder(Session currentSession, Class<M> entityClass) {
            super(currentSession, entityClass);
        }

        @Override
        public BaseProgrammingSubmissionQueryBuilder<M> whereContainerIs(String containerJid) {
            where(columnEq(AbstractProgrammingSubmissionModel_.containerJid, containerJid));
            return this;
        }

        @Override
        public BaseProgrammingSubmissionQueryBuilder<M> whereAuthorIs(String userJid) {
            where(columnEq(UnmodifiableModel_.createdBy, userJid));
            return this;
        }

        @Override
        public BaseProgrammingSubmissionQueryBuilder<M> whereProblemIs(String problemJid) {
            where(columnEq(AbstractProgrammingSubmissionModel_.problemJid, problemJid));
            return this;
        }

        @Override
        public BaseProgrammingSubmissionQueryBuilder<M> whereLastSubmissionIs(long submissionId) {
            where((cb, cq, root) -> cb.gt(root.get(UnmodifiableModel_.id), submissionId));
            return this;
        }
    }
}
