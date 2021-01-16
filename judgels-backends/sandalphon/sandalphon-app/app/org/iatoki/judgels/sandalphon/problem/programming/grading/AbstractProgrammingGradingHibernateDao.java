package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

public abstract class AbstractProgrammingGradingHibernateDao<M extends AbstractProgrammingGradingModel> extends JudgelsHibernateDao<M> implements BaseProgrammingGradingDao<M> {

    public AbstractProgrammingGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public final Map<String, List<M>> getBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, List<M>> result = Maps.newHashMap();

        for (List<String> partitionedSubmissionJids : Lists.partition(submissionJids, 1000)) {
            CriteriaBuilder cb = currentSession().getCriteriaBuilder();
            CriteriaQuery<M> query = cb.createQuery(getEntityClass());
            Root<M> root = query.from(getEntityClass());

            query.where(root.get(AbstractProgrammingGradingModel_.submissionJid).in(partitionedSubmissionJids));

            List<M> models = currentSession().createQuery(query).getResultList();

            for (M model : models) {
                if (result.containsKey(model.submissionJid)) {
                    result.get(model.submissionJid).add(model);
                } else {
                    @SuppressWarnings("unchecked")
                    List<M> list = Lists.newArrayList(model);

                    result.put(model.submissionJid, list);
                }
            }
        }

        return result;
    }

    @Override
    public final Map<String, M> getLatestBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, M> result = Maps.newHashMap();

        for (List<String> partitionedSubmissionJids : Lists.partition(submissionJids, 1000)) {
            CriteriaBuilder cb = currentSession().getCriteriaBuilder();
            CriteriaQuery<M> query = cb.createQuery(getEntityClass());
            Root<M> root = query.from(getEntityClass());

            query.select(cb.construct(
                    getEntityClass(),
                    root.get(Model_.id),
                    root.get(JudgelsModel_.jid),
                    root.get(AbstractProgrammingGradingModel_.submissionJid.getName()),
                    root.get(AbstractProgrammingGradingModel_.verdictCode.getName()),
                    root.get(AbstractProgrammingGradingModel_.verdictName.getName()),
                    root.get(AbstractProgrammingGradingModel_.score.getName())));

            query.where(root.get(AbstractProgrammingGradingModel_.submissionJid).in(partitionedSubmissionJids));

            List<M> models = currentSession().createQuery(query).getResultList();

            for (M model : models) {
                result.put(model.submissionJid, model);
            }
        }

        return result;
    }
}
