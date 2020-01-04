package judgels.jerahmeel.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ChapterProblemModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.api.problem.ProblemType;

public class ChapterProblemHibernateDao extends HibernateDao<ChapterProblemModel> implements ChapterProblemDao {
    @Inject
    public ChapterProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ChapterProblemModel> selectByProblemJid(String problemJid) {
        return selectByFilter(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.problemJid, problemJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public List<ChapterProblemModel> selectAllByProblemJids(Set<String> problemJids) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsIn(ChapterProblemModel_.problemJid, problemJids)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias) {
        return selectByFilter(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.alias, problemAlias)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public List<ChapterProblemModel> selectAllByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build(), options);
    }

    @Override
    public List<ChapterProblemModel> selectAllBundleByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .putColumnsEq(ChapterProblemModel_.type, ProblemType.BUNDLE.name())
                .build(), options);
    }

    @Override
    public List<ChapterProblemModel> selectAllProgrammingByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .putColumnsEq(ChapterProblemModel_.type, ProblemType.PROGRAMMING.name())
                .build(), options);
    }

    @Override
    public int selectCountProgrammingByChapterJid(String chapterJid) {
        return selectAllProgrammingByChapterJid(chapterJid, SelectionOptions.DEFAULT_ALL).size();
    }

    @Override
    public Map<String, Long> selectCountProgrammingByChapterJids(Set<String> chapterJids) {
        if (chapterJids.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<ChapterProblemModel> root = cq.from(getEntityClass());

        cq.select(cb.tuple(
                root.get(ChapterProblemModel_.chapterJid),
                cb.count(root)));

        cq.where(
                cb.equal(root.get(ChapterProblemModel_.status), "VISIBLE"),
                cb.equal(root.get(ChapterProblemModel_.type), ProblemType.PROGRAMMING.name()),
                root.get(ChapterProblemModel_.chapterJid).in(chapterJids));

        cq.groupBy(
                root.get(ChapterProblemModel_.chapterJid));

        return currentSession().createQuery(cq).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class)));

    }
}
