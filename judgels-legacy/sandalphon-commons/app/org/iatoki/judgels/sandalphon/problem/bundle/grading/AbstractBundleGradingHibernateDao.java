package org.iatoki.judgels.sandalphon.problem.bundle.grading;

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

public abstract class AbstractBundleGradingHibernateDao<M extends AbstractBundleGradingModel> extends AbstractJudgelsHibernateDao<M> implements BaseBundleGradingDao<M> {

    public AbstractBundleGradingHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public final Map<String, List<M>> getBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());
        Root<M> root = query.from(getModelClass());

        query.where(root.get(AbstractBundleGradingModel_.submissionJid).in(submissionJids));

        List<M> models = JPA.em().createQuery(query).getResultList();

        Map<String, List<M>> result = Maps.newHashMap();

        for (M model : models) {
            if (result.containsKey(model.submissionJid)) {
                result.get(model.submissionJid).add(model);
            } else {
                @SuppressWarnings("unchecked")
                List<M> list = Lists.newArrayList(model);

                result.put(model.submissionJid, list);
            }
        }

        return result;
    }
}
