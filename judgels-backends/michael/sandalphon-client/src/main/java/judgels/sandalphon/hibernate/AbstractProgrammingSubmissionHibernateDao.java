package judgels.sandalphon.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel_;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao;

public abstract class AbstractProgrammingSubmissionHibernateDao
        <M extends AbstractProgrammingSubmissionModel> extends JudgelsHibernateDao<M>
        implements BaseProgrammingSubmissionDao<M> {

    public AbstractProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<M> selectAllByContainerJid(String containerJid, Optional<Long> lastSubmissionId) {
        FilterOptions.Builder<M> filterOptions = new FilterOptions.Builder<>();
        filterOptions.putColumnsEq(AbstractProgrammingSubmissionModel_.containerJid, containerJid);
        lastSubmissionId.ifPresent(filterOptions::lastId);
        return selectAll(filterOptions.build(), new SelectionOptions.Builder().orderDir(OrderDir.ASC).build());
    }

    @Override
    public final Page<M> selectPaged(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options) {

        FilterOptions.Builder<M> filterOptions = new FilterOptions.Builder<>();
        containerJid.ifPresent(jid ->
                filterOptions.putColumnsEq(AbstractProgrammingSubmissionModel_.containerJid, jid));
        userJid.ifPresent(jid -> filterOptions.putColumnsEq(JudgelsModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(AbstractProgrammingSubmissionModel_.problemJid, jid));
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
}
