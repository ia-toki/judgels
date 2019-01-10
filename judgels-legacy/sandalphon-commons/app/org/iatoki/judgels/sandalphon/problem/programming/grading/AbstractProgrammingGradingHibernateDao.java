package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

public abstract class AbstractProgrammingGradingHibernateDao<M extends AbstractProgrammingGradingModel> extends AbstractJudgelsHibernateDao<M> implements BaseProgrammingGradingDao<M> {

    public AbstractProgrammingGradingHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public final Map<String, List<M>> getBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, List<M>> result = Maps.newHashMap();

        for (List<String> partitionedSubmissionJids : Lists.partition(submissionJids, 1000)) {
            CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
            CriteriaQuery<M> query = cb.createQuery(getModelClass());
            Root<M> root = query.from(getModelClass());

            query.where(root.get(AbstractProgrammingGradingModel_.submissionJid).in(partitionedSubmissionJids));

            List<M> models = JPA.em().createQuery(query).getResultList();

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
            CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
            CriteriaQuery<M> query = cb.createQuery(getModelClass());
            Root<M> root = query.from(getModelClass());

            query.select(cb.construct(
                    getModelClass(),
                    root.get(AbstractProgrammingGradingModel_.submissionJid.getName()),
                    root.get(AbstractProgrammingGradingModel_.verdictCode.getName()),
                    root.get(AbstractProgrammingGradingModel_.verdictName.getName()),
                    root.get(AbstractProgrammingGradingModel_.score.getName())));

            query.where(root.get(AbstractProgrammingGradingModel_.submissionJid).in(partitionedSubmissionJids));

            List<M> models = JPA.em().createQuery(query).getResultList();

            for (M model : models) {
                result.put(model.submissionJid, model);
            }
        }

        return result;
    }
}
