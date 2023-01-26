package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleGrading;
import judgels.sandalphon.api.submission.bundle.BundleGradingResult;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.api.submission.bundle.ItemGradingResult;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleGradingModel_;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.BundleSubmissionModel;
import judgels.sandalphon.persistence.BundleSubmissionModel_;
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
            FilterOptions<BundleGradingModel> filterOptions = new FilterOptions.Builder<BundleGradingModel>()
                    .putColumnsEq(BundleGradingModel_.submissionJid, sm.jid)
                    .build();
            List<BundleGradingModel> gradingModels = bundleGradingDao.selectAll(filterOptions, SelectionOptions.DEFAULT_ALL);
            return createSubmissionFromModels(sm, gradingModels);
        });
    }

    public List<BundleSubmission> getBundleSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        FilterOptions.Builder<BundleSubmissionModel> filterOptions = new FilterOptions.Builder<>();
        if (authorJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.containerJid, containerJid);
        }

        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_ALL)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.selectAll(filterOptions.build(), selectionOptions);

        return Lists.transform(submissionModels, this::createSubmissionFromModel);
    }

    public List<BundleSubmission> getBundleSubmissionsByJids(List<String> submissionJids) {
        Map<String, BundleSubmissionModel> models =
                bundleSubmissionDao.selectByJids(ImmutableSet.copyOf(submissionJids));
        return submissionJids.stream()
                .filter(models::containsKey)
                .map(models::get)
                .map(this::createSubmissionFromModel)
                .collect(Collectors.toList());
    }

    public Page<BundleSubmission> getPageOfBundleSubmissions(long pageIndex, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        FilterOptions.Builder<BundleSubmissionModel> filterOptions = new FilterOptions.Builder<>();
        if (authorJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterOptions.putColumnsEq(BundleSubmissionModel_.containerJid, containerJid);
        }

        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        long totalCount = bundleSubmissionDao.selectCount(filterOptions.build());
        List<BundleSubmissionModel> submissionModels = bundleSubmissionDao.selectAll(filterOptions.build(), selectionOptions);
        Map<String, List<BundleGradingModel>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<BundleSubmission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page.Builder<BundleSubmission>()
                .page(submissions)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
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
