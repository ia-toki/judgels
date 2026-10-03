package judgels.persistence.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.ProblemTagModel;
import judgels.persistence.model.ProblemTagModel_;

@Singleton
public class ProblemTagDao extends UnmodifiableDao<ProblemTagModel> {
    @Inject
    public ProblemTagDao(DaoData data) {
        super(data);
    }

    public List<ProblemTagModel> selectAllByProblemJid(String problemJid) {
        return select()
                .where(columnEq(ProblemTagModel_.problemJid, problemJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    public List<ProblemTagModel> selectAllByTags(Set<String> tags) {
        return select()
                .where(columnIn(ProblemTagModel_.tag, tags))
                .all();
    }

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

    public Map<String, Integer> selectPublicTagCounts() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<ProblemTagModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(ProblemTagModel_.tag),
                cb.count(root)));

        Subquery<ProblemTagModel> subquery = cq.subquery(getEntityClass());
        Root<ProblemTagModel> subroot = subquery.from(getEntityClass());
        subquery.where(
                cb.equal(subroot.get(ProblemTagModel_.problemJid), root.get(ProblemTagModel_.problemJid)),
                cb.equal(subroot.get(ProblemTagModel_.tag), "visibility-public"));
        subquery.select(subroot);

        cq.where(cb.exists(subquery));

        cq.groupBy(root.get(ProblemTagModel_.tag));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> (int) (long) tuple.get(1, Long.class)));
    }

    public Optional<ProblemTagModel> selectByProblemJidAndTag(String problemJid, String tag) {
        return select()
                .where(columnEq(ProblemTagModel_.problemJid, problemJid))
                .where(columnEq(ProblemTagModel_.tag, tag))
                .unique();
    }
}
