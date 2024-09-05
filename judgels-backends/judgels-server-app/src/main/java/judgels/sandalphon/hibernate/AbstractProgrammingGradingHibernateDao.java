package judgels.sandalphon.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel_;
import judgels.sandalphon.persistence.BaseProgrammingGradingDao;

public abstract class AbstractProgrammingGradingHibernateDao<M extends AbstractProgrammingGradingModel>
        extends JudgelsHibernateDao<M> implements BaseProgrammingGradingDao<M> {

    public AbstractProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<M> selectLatestBySubmissionJid(String submissionJid) {
        return select()
                .where(columnEq(AbstractProgrammingGradingModel_.submissionJid, submissionJid))
                .all()
                .stream()
                .findFirst();
    }

    @Override
    public Map<String, M> selectAllLatestBySubmissionJids(Collection<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, M> result = Maps.newHashMap();

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = criteriaQuery();
        Root<M> root = query.from(getGradingModelClass());

        query.select(
                cb.construct(
                        getGradingModelClass(),
                        root.get(Model_.id),
                        root.get(JudgelsModel_.jid),
                        root.get(AbstractProgrammingGradingModel_.submissionJid),
                        root.get(AbstractProgrammingGradingModel_.verdictCode),
                        root.get(AbstractProgrammingGradingModel_.verdictName),
                        root.get(AbstractProgrammingGradingModel_.score)));

        query.where(root.get(AbstractProgrammingGradingModel_.submissionJid).in(submissionJids));
        query.orderBy(cb.asc(root.get(Model_.id)));

        List<M> models = currentSession().createQuery(query).getResultList();

        for (M model : models) {
            result.put(model.submissionJid, model);
        }

        return ImmutableMap.copyOf(result);
    }

    @Override
    public Map<String, M> selectAllLatestWithDetailsBySubmissionJids(Collection<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, M> result = Maps.newHashMap();

        List<M> models = select()
                .where(columnIn(AbstractProgrammingGradingModel_.submissionJid, submissionJids))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();

        for (M model : models) {
            result.put(model.submissionJid, model);
        }

        return ImmutableMap.copyOf(result);
    }

    @Override
    public void dump(PrintWriter output, Collection<String> submissionJids) {
        List<M> results = select().where(columnIn(AbstractProgrammingGradingModel_.submissionJid, submissionJids)).orderBy(Model_.ID, OrderDir.ASC).all();
        if (results.isEmpty()) {
            return;
        }

        output.write("INSERT IGNORE INTO uriel_contest_programming_grading (jid, submissionJid, verdictCode, verdictName, score, details, createdBy, createdAt, updatedAt) VALUES\n");

        for (int i = 0; i < results.size(); i++) {
            M m = results.get(i);
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
