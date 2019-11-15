package judgels.jerahmeel.problemset;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class ProblemSetStore {
    private final ProblemSetDao problemSetDao;

    @Inject
    public ProblemSetStore(ProblemSetDao problemSetDao) {
        this.problemSetDao = problemSetDao;

    }

    public Page<ProblemSet> getProblemSets(Optional<String> name, Optional<Integer> page) {
        SearchOptions.Builder searchOptions = new SearchOptions.Builder();
        name.ifPresent(e -> searchOptions.putTerms("name", e));

        SelectionOptions.Builder selectionOptions = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(selectionOptions::page);

        Page<ProblemSetModel> models = problemSetDao.selectPaged(searchOptions.build(), selectionOptions.build());
        return models.mapPage(p -> Lists.transform(p, ProblemSetStore::fromModel));
    }

    private static ProblemSet fromModel(ProblemSetModel model) {
        return new ProblemSet.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
