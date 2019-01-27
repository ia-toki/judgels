package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import judgels.persistence.api.SelectionOptions;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingResult;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BaseBundleGradingDao;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingModel;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingModel_;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;
import play.data.DynamicForm;

import javax.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public abstract class AbstractBundleSubmissionServiceImpl<SM extends AbstractBundleSubmissionModel, GM extends AbstractBundleGradingModel> implements BundleSubmissionService {

    private final BaseBundleSubmissionDao<SM> bundleSubmissionDao;
    private final BaseBundleGradingDao<GM> bundleGradingDao;
    private final BundleProblemGrader bundleProblemGrader;

    protected AbstractBundleSubmissionServiceImpl(BaseBundleSubmissionDao<SM> bundleSubmissionDao, BaseBundleGradingDao<GM> bundleGradingDao, BundleProblemGrader bundleProblemGrader) {
        this.bundleSubmissionDao = bundleSubmissionDao;
        this.bundleGradingDao = bundleGradingDao;
        this.bundleProblemGrader = bundleProblemGrader;
    }

    @Override
    public BundleSubmission findBundleSubmissionById(long submissionId) throws BundleSubmissionNotFoundException {
        SM submissionModel = bundleSubmissionDao.findById(submissionId);
        List<GM> gradingModels = bundleGradingDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(AbstractBundleGradingModel_.submissionJid, submissionModel.jid), 0, -1);

        return BundleSubmissionServiceUtils.createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public BundleSubmission findBundleSubmissionByJid(String submissionJid) {
        SM submissionModel = bundleSubmissionDao.findByJid(submissionJid);
        List<GM> gradingModels = bundleGradingDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(AbstractBundleGradingModel_.submissionJid, submissionModel.jid), 0, -1);

        return BundleSubmissionServiceUtils.createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public List<Instant> getAllBundleSubmissionsSubmitTime() {
        return bundleSubmissionDao.getAllSubmissionsSubmitTime();
    }

    @Override
    public List<BundleSubmission> getAllBundleSubmissions() {
        List<SM> submissionModels = bundleSubmissionDao.getAll();
        Map<String, List<GM>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> BundleSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<BundleSubmission> getBundleSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid) {
        List<SM> submissionModels = bundleSubmissionDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.<SingularAttribute<? super SM, ? extends Object>, String>of(AbstractBundleSubmissionModel_.containerJid, containerJid, AbstractBundleSubmissionModel_.problemJid, problemJid, AbstractBundleSubmissionModel_.createdBy, userJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> BundleSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<BundleSubmission> getBundleSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, ? extends Object>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, ? extends Object>, String> filterColumns = filterColumnsBuilder.build();

        List<SM> submissionModels = bundleSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, 0, -1);

        return Lists.transform(submissionModels, m -> BundleSubmissionServiceUtils.createSubmissionFromModel(m));
    }

    @Override
    public List<BundleSubmission> getBundleSubmissionsByJids(List<String> submissionJids) {
        List<SM> submissionModels = bundleSubmissionDao.getByJids(submissionJids);

        return Lists.transform(submissionModels, m -> BundleSubmissionServiceUtils.createSubmissionFromModel(m));
    }

    @Override
    public Page<BundleSubmission> getPageOfBundleSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, ? extends Object>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractBundleSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, ? extends Object>, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = bundleSubmissionDao.countByFiltersEq("", filterColumns);
        List<SM> submissionModels = bundleSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, pageIndex * pageSize, pageSize);
        Map<String, List<GM>> gradingModelsMap = bundleGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<BundleSubmission> submissions = Lists.transform(submissionModels, m -> BundleSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page<>(submissions, totalRowsCount, pageIndex, pageSize);
    }

    @Override
    public final String submit(String problemJid, String containerJid, BundleAnswer answer, String userJid, String userIpAddress) {
        SM submissionModel = bundleSubmissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.containerJid = containerJid;

        bundleSubmissionDao.persist(submissionModel, userJid, userIpAddress);

        grade(submissionModel, answer, userJid, userIpAddress);

        return submissionModel.jid;
    }

    @Override
    public final void regrade(String submissionJid, BundleAnswer answer, String userJid, String userIpAddress) {
        SM submissionModel = bundleSubmissionDao.findByJid(submissionJid);

        grade(submissionModel, answer, userJid, userIpAddress);
    }

    @Override
    public void afterGrade(String gradingJid, BundleAnswer answer) {
        // To be overridden if needed
    }

    @Override
    public void storeSubmissionFiles(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid, BundleAnswer answer) {
        List<FileSystemProvider> fileSystemProviders = Lists.newArrayList(localFileSystemProvider);
        if (remoteFileSystemProvider != null) {
            fileSystemProviders.add(remoteFileSystemProvider);
        }

        for (FileSystemProvider fileSystemProvider : fileSystemProviders) {
            try {
                fileSystemProvider.createDirectory(ImmutableList.of(submissionJid));

                fileSystemProvider.writeToFile(ImmutableList.of(submissionJid, "answer.json"), new Gson().toJson(answer));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode) {
        return new BundleAnswer(data.data(), languageCode);
    }

    @Override
    public BundleAnswer createBundleAnswerFromPastSubmission(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid) throws IOException {
        FileSystemProvider fileSystemProvider;

        if (localFileSystemProvider.directoryExists(ImmutableList.of(submissionJid))) {
            fileSystemProvider = localFileSystemProvider;
        } else {
            fileSystemProvider = remoteFileSystemProvider;
        }

        return new Gson().fromJson(fileSystemProvider.readFromFile(ImmutableList.of(submissionJid, "answer.json")), BundleAnswer.class);
    }

    private void grade(SM submissionModel, BundleAnswer answer, String userJid, String userIpAddress) {
        try {
            BundleGradingResult bundleGradingResult = bundleProblemGrader.gradeBundleProblem(submissionModel.problemJid, answer);

            if (bundleGradingResult != null) {
                GM gradingModel = bundleGradingDao.createGradingModel();

                gradingModel.submissionJid = submissionModel.jid;
                gradingModel.score = bundleGradingResult.getScore();
                gradingModel.details = bundleGradingResult.getDetailsAsJson();

                bundleGradingDao.persist(gradingModel, userJid, userIpAddress);

                afterGrade(gradingModel.jid, answer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
