package judgels.sandalphon.hibernate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.Model_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractGradingModel;
import judgels.sandalphon.persistence.AbstractGradingModel_;
import judgels.sandalphon.persistence.BaseGradingDao;

public abstract class AbstractGradingHibernateDao<M extends AbstractGradingModel> extends JudgelsHibernateDao<M>
        implements BaseGradingDao<M> {

    public AbstractGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<M> selectLatestBySubmissionJid(String submissionJid) {
        FilterOptions<M> filterOptions = new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractGradingModel_.submissionJid, submissionJid)
                .build();

        return selectAll(filterOptions).stream().findFirst();
    }

    @Override
    public Map<String, M> selectAllLatestBySubmissionJids(Set<String> submissionJids) {
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
                        root.get(AbstractGradingModel_.submissionJid),
                        root.get(AbstractGradingModel_.verdictCode),
                        root.get(AbstractGradingModel_.verdictName),
                        root.get(AbstractGradingModel_.score)));

        query.where(root.get(AbstractGradingModel_.submissionJid).in(submissionJids));
        query.orderBy(cb.asc(root.get(Model_.id)));

        List<M> models = currentSession().createQuery(query).getResultList();

        for (M model : models) {
            result.put(model.submissionJid, model);
        }

        return ImmutableMap.copyOf(result);
    }
}
