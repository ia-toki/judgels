package judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.bundle.BundleGrading;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.BundleSubmissionModel;
import judgels.sandalphon.persistence.BundleSubmissionModel_;

public class BundleSubmissionStore {
    private final ObjectMapper mapper;
    private final BundleSubmissionDao submissionDao;
    private final BundleGradingDao gradingDao;

    @Inject
    public BundleSubmissionStore(
            ObjectMapper mapper,
            BundleSubmissionDao submissionDao,
            BundleGradingDao gradingDao) {

        this.mapper = mapper;
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
    }

    public Page<BundleSubmission> getSubmissions(String problemJid, int pageIndex) {
        FilterOptions<BundleSubmissionModel> filterOptions = new FilterOptions.Builder<BundleSubmissionModel>()
                .putColumnsEq(BundleSubmissionModel_.problemJid, problemJid)
                .build();

        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page(pageIndex)
                .build();

        long totalCount = submissionDao.selectCount(filterOptions);
        List<BundleSubmissionModel>
                submissionModels = submissionDao.selectAll(filterOptions, selectionOptions);
        Map<String, List<BundleGradingModel>>
                gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<BundleSubmission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page.Builder<BundleSubmission>()
                .page(submissions)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
    }

    private BundleSubmission createSubmissionFromModels(BundleSubmissionModel submissionModel, List<BundleGradingModel> gradingModels) {
        return new BundleSubmission.Builder()
                .id(submissionModel.id)
                .jid(submissionModel.jid)
                .problemJid(submissionModel.problemJid)
                .authorJid(submissionModel.createdBy)
                .time(submissionModel.createdAt)
                .latestGrading(createGradingFromModel(gradingModels.get(gradingModels.size() - 1)))
                .build();
    }

    private BundleGrading createGradingFromModel(BundleGradingModel model) {
        Map<String, ItemGradingResult> details;
        try {
            details = mapper.readValue(model.details, new TypeReference<Map<String, ItemGradingResult>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BundleGrading.Builder()
                .id(model.id)
                .jid(model.jid)
                .score(model.score)
                .details(details)
                .build();
    }
}
