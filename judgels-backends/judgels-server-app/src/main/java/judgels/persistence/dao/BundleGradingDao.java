package judgels.persistence.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.BundleGradingModel;
import judgels.persistence.model.BundleGradingModel_;

public final class BundleGradingDao extends JudgelsDao<BundleGradingModel> {

    @Inject
    public BundleGradingDao(DaoData data) {
        super(data);
    }

    public List<BundleGradingModel> selectAllBySubmissionJid(String submissionJid) {
        return select()
                .where(columnEq(BundleGradingModel_.submissionJid, submissionJid))
                .all();
    }

    public Map<String, List<BundleGradingModel>> getBySubmissionJids(List<String> submissionJids) {
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
