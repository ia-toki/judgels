package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.util.List;
import java.util.Map;

public abstract class AbstractBundleGradingHibernateDao<M extends AbstractBundleGradingModel> extends JudgelsHibernateDao<M> implements BaseBundleGradingDao<M> {

    public AbstractBundleGradingHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public final Map<String, List<M>> getBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());
        Root<M> root = query.from(getEntityClass());

        query.where(root.get(AbstractBundleGradingModel_.submissionJid).in(submissionJids));

        List<M> models = currentSession().createQuery(query).getResultList();

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
