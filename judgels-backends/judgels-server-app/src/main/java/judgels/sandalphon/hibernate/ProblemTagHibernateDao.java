package judgels.sandalphon.hibernate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.sandalphon.persistence.ProblemTagDao;
import judgels.sandalphon.persistence.ProblemTagModel;
import judgels.sandalphon.persistence.ProblemTagModel_;

@Singleton
public class ProblemTagHibernateDao extends UnmodifiableHibernateDao<ProblemTagModel> implements ProblemTagDao {
    @Inject
    public ProblemTagHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<ProblemTagModel> selectAllByProblemJid(String problemJid) {
        return selectAll(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsEq(ProblemTagModel_.problemJid, problemJid)
                .build(), new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderDir(OrderDir.ASC)
                .build());
    }

    @Override
    public List<ProblemTagModel> selectAllByTags(Set<String> tags) {
        return selectAll(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsIn(ProblemTagModel_.tag, tags)
                .build());
    }

    @Override
    public Map<String, Integer> selectTagCounts() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<ProblemTagModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(ProblemTagModel_.tag),
                cb.count(root)));

        cq.groupBy(root.get(ProblemTagModel_.tag));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> (int) (long) tuple.get(1, Long.class)));
    }

    @Override
    public Map<String, Integer> selectPublicTagCounts() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<ProblemTagModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(ProblemTagModel_.tag),
                cb.count(root)));

        Subquery<ProblemTagModel> sq = cq.subquery(getEntityClass());
        Root<ProblemTagModel> subroot = sq.from(getEntityClass());
        sq.where(
                cb.equal(subroot.get(ProblemTagModel_.problemJid), root.get(ProblemTagModel_.problemJid)),
                cb.equal(subroot.get(ProblemTagModel_.tag), "visibility-public"));
        sq.select(subroot);

        cq.where(cb.exists(sq));

        cq.groupBy(root.get(ProblemTagModel_.tag));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> (int) (long) tuple.get(1, Long.class)));
    }

    @Override
    public Optional<ProblemTagModel> selectByProblemJidAndTag(String problemJid, String tag) {
        return selectByFilter(new FilterOptions.Builder<ProblemTagModel>()
                .putColumnsEq(ProblemTagModel_.problemJid, problemJid)
                .putColumnsEq(ProblemTagModel_.tag, tag)
                .build());
    }
}
