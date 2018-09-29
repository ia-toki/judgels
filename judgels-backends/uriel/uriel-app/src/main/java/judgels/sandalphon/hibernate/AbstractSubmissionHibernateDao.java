package judgels.sandalphon.hibernate;

import java.time.Clock;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sandalphon.persistence.AbstractSubmissionModel_;
import judgels.sandalphon.persistence.BaseSubmissionDao;
import org.hibernate.SessionFactory;

public abstract class AbstractSubmissionHibernateDao<M extends AbstractSubmissionModel> extends JudgelsHibernateDao<M>
        implements BaseSubmissionDao<M> {

    public AbstractSubmissionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public final Page<M> selectPaged(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options) {

        FilterOptions.Builder<M> filterOptions = new FilterOptions.Builder<>();
        filterOptions.putColumnsEq(AbstractSubmissionModel_.containerJid, containerJid);
        userJid.ifPresent(jid -> filterOptions.putColumnsEq(JudgelsModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(AbstractSubmissionModel_.problemJid, jid));
        lastSubmissionId.ifPresent(filterOptions::lastId);

        return selectPaged(filterOptions.build(), options);
    }

    @Override
    public Map<String, Long> selectCounts(String containerJid, String userJid, Set<String> problemJids) {
        if (problemJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<M> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(AbstractSubmissionModel_.problemJid),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(AbstractSubmissionModel_.containerJid), containerJid),
                cb.equal(root.get(JudgelsModel_.createdBy), userJid),
                root.get(AbstractSubmissionModel_.problemJid).in(problemJids));

        cq.groupBy(
                root.get(AbstractSubmissionModel_.containerJid),
                root.get(JudgelsModel_.createdBy),
                root.get(AbstractSubmissionModel_.problemJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));
    }
}
