package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;
import judgels.fs.FileSystem;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleGrading;
import judgels.sandalphon.api.submission.bundle.BundleGradingResult;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingModel_;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;
import play.data.DynamicForm;

public class BundleSubmissionStore {
    private final ObjectMapper mapper;
    private final BundleSubmissionDao bundleSubmissionDao;
    private final BundleGradingDao bundleGradingDao;
    private final BundleProblemGrader bundleProblemGrader;

    @Inject
    public BundleSubmissionStore(
            ObjectMapper mapper,
            BundleSubmissionDao bundleSubmissionDao,
            BundleGradingDao bundleGradingDao,
            BundleProblemGrader bundleProblemGrader) {

        this.mapper = mapper;
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.bundleProblemGrader = bundleProblemGrader;
    }

    public Optional<BundleSubmission> findBundleSubmissionById(long submissionId) {
        return bundleSubmissionDao.select(submissionId).map(sm -> {
            List<BundleGradingModel> gradingModels = bundleGradingDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(BundleGradingModel_.submissionJid, sm.jid), 0, -1);
            return createSubmissionFromModels(sm, gradingModels);
        });
    }

    public List<BundleSubmission> getAllBundleSubmissions() {
        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.getAll();
        Map<String, List<BundleGradingModel>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    public List<BundleSubmission> getBundleSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super BundleSubmissionModel, ?>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super BundleSubmissionModel, ?>, String> filterColumns = filterColumnsBuilder.build();

        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, 0, -1);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    public List<BundleSubmission> getBundleSubmissionsByJids(List<String> submissionJids) {
        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.getByJids(submissionJids);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    public Page<BundleSubmission> getPageOfBundleSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super BundleSubmissionModel, ?>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(BundleSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super BundleSubmissionModel, ?>, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = bundleSubmissionDao.countByFiltersEq("", filterColumns);
        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, pageIndex * pageSize, pageSize);
        Map<String, List<BundleGradingModel>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<BundleSubmission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page.Builder<BundleSubmission>()
                .page(submissions)
                .totalCount(totalRowsCount)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    public final String submit(String problemJid, String containerJid, BundleAnswer answer) {
        BundleSubmissionModel submissionModel = new BundleSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.containerJid = containerJid;

        bundleSubmissionDao.insert(submissionModel);

        grade(submissionModel, answer);

        return submissionModel.jid;
    }

    public final void regrade(String submissionJid, BundleAnswer answer) {
        BundleSubmissionModel submissionModel = bundleSubmissionDao.findByJid(submissionJid);

        grade(submissionModel, answer);
    }

    public void storeSubmissionFiles(FileSystem localFs, FileSystem remoteFs, String submissionJid, BundleAnswer answer) {
        List<FileSystem> fileSystemProviders = Lists.newArrayList(localFs);
        if (remoteFs != null) {
            fileSystemProviders.add(remoteFs);
        }

        for (FileSystem fileSystemProvider : fileSystemProviders) {
            fileSystemProvider.createDirectory(Paths.get(submissionJid));

            fileSystemProvider.writeToFile(Paths.get(submissionJid, "answer.json"), writeObj(answer));
        }
    }

    public BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode) {
        return new BundleAnswer.Builder()
                .answers(data.rawData())
                .languageCode(languageCode)
                .build();
    }

    public BundleAnswer createBundleAnswerFromPastSubmission(FileSystem localFs, FileSystem remoteFs, String submissionJid) {
        FileSystem fileSystemProvider;

        if (localFs.directoryExists(Paths.get(submissionJid))) {
            fileSystemProvider = localFs;
        } else {
            fileSystemProvider = remoteFs;
        }

        try {
            return mapper.readValue(fileSystemProvider.readFromFile(Paths.get(submissionJid, "answer.json")), BundleAnswer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void grade(BundleSubmissionModel submissionModel, BundleAnswer answer) {
        BundleGradingResult result = bundleProblemGrader.gradeBundleProblem(submissionModel.problemJid, answer);

        if (result != null) {
            BundleGradingModel model = new BundleGradingModel();

            model.submissionJid = submissionModel.jid;
            model.score = (int) result.getScore();
            model.details = writeObj(result.getDetails());

            bundleGradingDao.insert(model);
        }
    }

    private BundleSubmission createSubmissionFromModel(BundleSubmissionModel submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
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

    private String writeObj(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
