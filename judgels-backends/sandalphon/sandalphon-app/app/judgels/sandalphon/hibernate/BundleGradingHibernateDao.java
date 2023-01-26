package judgels.sandalphon.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleGradingModel_;

@Singleton
public final class BundleGradingHibernateDao extends JudgelsHibernateDao<BundleGradingModel> implements BundleGradingDao {

    @Inject
    public BundleGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Map<String, List<judgels.sandalphon.persistence.BundleGradingModel>> getBySubmissionJids(List<String> submissionJids) {
        if (submissionJids.isEmpty()) {
            return ImmutableMap.of();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<BundleGradingModel> query = cb.createQuery(getEntityClass());
        Root<BundleGradingModel> root = query.from(getEntityClass());

        query.where(root.get(BundleGradingModel_.submissionJid).in(submissionJids));

        List<BundleGradingModel> models = currentSession().createQuery(query).getResultList();

        Map<String, List<BundleGradingModel>> result = Maps.newHashMap();

        for (BundleGradingModel model : models) {
            if (result.containsKey(model.submissionJid)) {
                result.get(model.submissionJid).add(model);
            } else {
                @SuppressWarnings("unchecked")
                List<BundleGradingModel> list = Lists.newArrayList(model);

                result.put(model.submissionJid, list);
            }
        }

        return result;
    }
}
