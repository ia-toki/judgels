package judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemModel_;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;

public class ProblemSearchStore {
    private final ProblemDao problemDao;
    private final ProblemPartnerDao problemPartnerDao;
    private final ProblemTagStore problemTagStore;

    @Inject
    public ProblemSearchStore(
            ProblemDao problemDao,
            ProblemPartnerDao problemPartnerDao,
            ProblemTagStore problemTagStore) {

        this.problemDao = problemDao;
        this.problemPartnerDao = problemPartnerDao;
        this.problemTagStore = problemTagStore;
    }

    public Page<Problem> searchProblems(long pageIndex, String orderBy, String orderDir, String filterString, List<String> tags, String userJid, boolean isAdmin) {
        FilterOptions.Builder<ProblemModel> filterOptionsBuilder;
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();


        filterOptionsBuilder = new FilterOptions.Builder<ProblemModel>()
                .putColumnsLike(ProblemModel_.slug, filterString)
                .putColumnsLike(ProblemModel_.additionalNote, filterString);

        Set<String> allowedProblemJids = null;
        if (!isAdmin) {
            allowedProblemJids = filterProblemJidsByUserJid(userJid);
        }
        if (tags != null) {
            allowedProblemJids = problemTagStore.filterProblemJidsByTags(allowedProblemJids, ImmutableSet.copyOf(tags));
        }
        if (allowedProblemJids != null) {
            filterOptionsBuilder.putColumnsIn(ProblemModel_.jid, allowedProblemJids);
        }

        FilterOptions<ProblemModel> filterOptions = filterOptionsBuilder.build();
        long totalCount = problemDao.selectCount(filterOptions);
        List<ProblemModel> models = problemDao.selectAll(filterOptions, selectionOptions);

        List<Problem> problems = Lists.transform(models, ProblemStore::createProblemFromModel);
        return new Page.Builder<Problem>()
                .page(problems)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
    }

    private Set<String> filterProblemJidsByUserJid(String userJid) {
        List<String> problemJidsWhereIsAuthor = problemDao.getJidsByAuthorJid(userJid);
        List<String> problemJidsWhereIsPartner = problemPartnerDao.getProblemJidsByPartnerJid(userJid);

        ImmutableSet.Builder<String> allowedProblemJidsBuilder = ImmutableSet.builder();
        allowedProblemJidsBuilder.addAll(problemJidsWhereIsAuthor);
        allowedProblemJidsBuilder.addAll(problemJidsWhereIsPartner);
        return allowedProblemJidsBuilder.build();
    }
}
