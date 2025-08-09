package judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.bundle.BundleGrading;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.BundleSubmissionModel;

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

    public Page<BundleSubmission> getSubmissions(String problemJid, int pageNumber, int pageSize) {
        Page<BundleSubmissionModel> submissionModels = submissionDao
                .selectByProblemJid(problemJid)
                .paged(pageNumber, pageSize);
        Map<String, List<BundleGradingModel>>
                gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels.getPage(), m -> m.jid));

        List<BundleSubmission> submissions = Lists.transform(submissionModels.getPage(), m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page.Builder<BundleSubmission>()
                .page(submissions)
                .totalCount(submissionModels.getTotalCount())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }

    public Optional<BundleSubmission> getSubmissionById(int submissionId) {
        return submissionDao.selectById(submissionId).map(sm -> {
            List<BundleGradingModel> gradingModels = gradingDao.selectAllBySubmissionJid(sm.jid);
            return createSubmissionFromModels(sm, gradingModels);
        });
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
