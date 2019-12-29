package judgels.jerahmeel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.Model_;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.apache.commons.lang3.math.NumberUtils;

public class ProblemSetHibernateDao extends JudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {
    @Inject
    public ProblemSetHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ProblemSetModel> selectBySlug(String problemSetSlug) {
        // if no slug matches, treat it as ID for legacy reasons
        return selectByFilter(new FilterOptions.Builder<ProblemSetModel>()
                .addCustomPredicates((cb, cq, root) -> cb.or(
                        cb.equal(root.get(ProblemSetModel_.slug), problemSetSlug),
                        cb.equal(root.get(Model_.id), NumberUtils.toInt(problemSetSlug, 0))))
                .build());
    }

    @Override
    public Page<ProblemSetModel> selectPaged(
            Optional<String> archiveJid,
            SearchOptions searchOptions,
            SelectionOptions options) {

        return selectPaged(createFilterOptions(archiveJid, searchOptions).build(), options);
    }

    private static FilterOptions.Builder<ProblemSetModel> createFilterOptions(
            Optional<String> archiveJid,
            SearchOptions searchOptions) {

        FilterOptions.Builder<ProblemSetModel> filterOptions = new FilterOptions.Builder<>();

        archiveJid.ifPresent(jid -> filterOptions.putColumnsEq(ProblemSetModel_.archiveJid, jid));
        if (searchOptions.getTerms().containsKey("name")) {
            filterOptions.putColumnsLike(ProblemSetModel_.name, searchOptions.getTerms().get("name"));
        }

        return filterOptions;
    }
}
