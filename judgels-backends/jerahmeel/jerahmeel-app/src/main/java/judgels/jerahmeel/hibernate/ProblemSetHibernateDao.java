package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public class ProblemSetHibernateDao extends JudgelsHibernateDao<ProblemSetModel> implements ProblemSetDao {
    @Inject
    public ProblemSetHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Page<ProblemSetModel> selectPaged(SearchOptions searchOptions, SelectionOptions options) {
        return selectPaged(createFilterOptions(searchOptions).build(), options);
    }

    private static FilterOptions.Builder<ProblemSetModel> createFilterOptions(SearchOptions searchOptions) {
        FilterOptions.Builder<ProblemSetModel> filterOptions = new FilterOptions.Builder<>();

        if (searchOptions.getTerms().containsKey("name")) {
            filterOptions.putColumnsLike(ProblemSetModel_.name, searchOptions.getTerms().get("name"));
        }

        return filterOptions;
    }
}
