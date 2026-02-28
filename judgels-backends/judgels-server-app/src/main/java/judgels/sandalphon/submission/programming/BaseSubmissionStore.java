package judgels.sandalphon.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.CursorPage;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;
import judgels.sandalphon.persistence.BaseProgrammingGradingDao;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao.BaseProgrammingSubmissionQueryBuilder;

public class BaseSubmissionStore<
        SM extends AbstractProgrammingSubmissionModel,
        GM extends AbstractProgrammingGradingModel>
        implements SubmissionStore {

    private final BaseProgrammingSubmissionDao<SM> submissionDao;
    private final BaseProgrammingGradingDao<GM> gradingDao;
    private final ObjectMapper mapper;

    public BaseSubmissionStore(
            BaseProgrammingSubmissionDao<SM> submissionDao,
            BaseProgrammingGradingDao<GM> gradingDao,
            ObjectMapper mapper) {

        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.mapper = mapper;
    }

    @Override
    public Optional<Submission> getSubmissionById(long submissionId) {
        return submissionDao.selectById(submissionId).map(model -> {
            Optional<GM> gradingModel = gradingDao.selectLatestBySubmissionJid(model.jid);
            return submissionFromModels(model, gradingModel.orElse(null));
        });
    }

    @Override
    public Optional<Submission> getSubmissionByJid(String submissionJid) {
        return submissionDao.selectByJid(submissionJid).map(model -> {
            Optional<GM> gradingModel = gradingDao.selectLatestBySubmissionJid(model.jid);
            return submissionFromModels(model, gradingModel.orElse(null));
        });
    }

    @Override
    public List<Submission> getSubmissionsForScoreboard(
            String containerJid,
            boolean withGradingDetails,
            long lastSubmissionId) {

        List<SM> submissionModels = submissionDao
                .select()
                .whereContainerIs(containerJid)
                .whereLastSubmissionIs(lastSubmissionId)
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();

        var submissionJids = Lists.transform(submissionModels, m -> m.jid);
        Map<String, GM> gradingModels = withGradingDetails
                ? gradingDao.selectAllLatestWithDetailsBySubmissionJids(submissionJids)
                : gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        gradingDao.clear();

        return Lists.transform(submissionModels, sm -> submissionFromModels(sm, gradingModels.get(sm.jid)));
    }

    @Override
    public Page<Submission> getSubmissionsForDownload(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            int pageSize) {

        BaseProgrammingSubmissionQueryBuilder<SM> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (userJid.isPresent()) {
            query.whereAuthorIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }
        if (lastSubmissionId.isPresent()) {
            query.whereLastSubmissionIs(lastSubmissionId.get());
        }

        Page<SM> submissionModels = query
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .paged(1, pageSize);

        return getSubmissions(submissionModels);
    }

    @Override
    public Page<Submission> getSubmissionsForStats(
            Optional<String> containerJid,
            Optional<Long> lastSubmissionId,
            int pageSize) {

        BaseProgrammingSubmissionQueryBuilder<SM> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (lastSubmissionId.isPresent()) {
            query.whereLastSubmissionIs(lastSubmissionId.get());
        }

        Page<SM> submissionModels = query
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .paged(1, pageSize);

        var submissionJids = Lists.transform(submissionModels.getPage(), m -> m.jid);
        Map<String, GM> gradingModels = gradingDao.selectAllLatestWithDetailsBySubmissionJids(submissionJids);

        return submissionModels.mapPage(p ->
                Lists.transform(p, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))));
    }

    @Override
    public Page<Submission> getSubmissions(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            int pageNumber,
            int pageSize) {

        BaseProgrammingSubmissionQueryBuilder<SM> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (userJid.isPresent()) {
            query.whereAuthorIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        Page<SM> submissionModels = query
                .paged(pageNumber, pageSize);

        return getSubmissions(submissionModels);
    }

    private Page<Submission> getSubmissions(Page<SM> submissionModels) {
        var submissionJids = Lists.transform(submissionModels.getPage(), m -> m.jid);
        Map<String, GM> gradingModels = gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        return submissionModels.mapPage(p ->
                Lists.transform(p, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))));
    }

    @Override
    public CursorPage<Submission> getSubmissionsCursor(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> beforeId,
            Optional<Long> afterId,
            int pageSize) {

        if (beforeId.isPresent() && afterId.isPresent()) {
            throw new IllegalArgumentException("Cannot specify both beforeId and afterId");
        }

        BaseProgrammingSubmissionQueryBuilder<SM> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (userJid.isPresent()) {
            query.whereAuthorIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        boolean isAfter = afterId.isPresent();

        if (beforeId.isPresent()) {
            query.whereIdLessThan(beforeId.get());
            query.orderBy(UnmodifiableModel_.ID, OrderDir.DESC);
        } else if (afterId.isPresent()) {
            query.whereIdGreaterThan(afterId.get());
            query.orderBy(UnmodifiableModel_.ID, OrderDir.ASC);
        } else {
            query.orderBy(UnmodifiableModel_.ID, OrderDir.DESC);
        }

        List<SM> submissionModels = query.list(pageSize + 1);

        boolean hasMore = submissionModels.size() > pageSize;
        if (hasMore) {
            submissionModels = submissionModels.subList(0, pageSize);
        }

        if (isAfter) {
            submissionModels = Lists.reverse(submissionModels);
        }

        boolean hasNextPage;
        boolean hasPreviousPage;

        if (beforeId.isPresent()) {
            hasNextPage = true;
            hasPreviousPage = hasMore;
        } else if (afterId.isPresent()) {
            hasNextPage = hasMore;
            hasPreviousPage = true;
        } else {
            hasNextPage = false;
            hasPreviousPage = hasMore;
        }

        var submissionJids = Lists.transform(submissionModels, m -> m.jid);
        Map<String, GM> gradingModels = gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        return new CursorPage.Builder<Submission>()
                .page(Lists.transform(submissionModels, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))))
                .hasNextPage(hasNextPage)
                .hasPreviousPage(hasPreviousPage)
                .build();
    }

    @Override
    public List<Submission> getUserProblemSubmissions(
            String containerJid,
            String userJid,
            String problemJid) {

        List<SM> submissionModels = submissionDao
                .select()
                .whereContainerIs(containerJid)
                .whereAuthorIs(userJid)
                .whereProblemIs(problemJid)
                .all();

        var submissionJids = Lists.transform(submissionModels, m -> m.jid);
        Map<String, GM> gradingModels = gradingDao.selectAllLatestWithDetailsBySubmissionJids(submissionJids);

        return Lists.transform(submissionModels, sm -> submissionFromModels(sm, gradingModels.get(sm.jid)));
    }

    @Override
    public Optional<Submission> getLatestSubmission(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        BaseProgrammingSubmissionQueryBuilder<SM> query = submissionDao.select();

        if (containerJid.isPresent()) {
            query.whereContainerIs(containerJid.get());
        }
        if (userJid.isPresent()) {
            query.whereAuthorIs(userJid.get());
        }
        if (problemJid.isPresent()) {
            query.whereProblemIs(problemJid.get());
        }

        Page<SM> submissionModels = query
                .paged(1, 1);

        List<Submission> submissions = getSubmissions(submissionModels).getPage();
        if (submissions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(submissions.get(0));
    }

    @Override
    public long getTotalSubmissions(String containerJid, String userJid, String problemJid) {
        Map<String, Long> map = submissionDao.selectCounts(containerJid, userJid, ImmutableSet.of(problemJid));
        return map.getOrDefault(problemJid, 0L);
    }

    @Override
    public Map<String, Long> getTotalSubmissionsMap(String containerJid, String userJid, Collection<String> problemJids) {
        Map<String, Long> map = submissionDao.selectCounts(containerJid, userJid, problemJids);
        return problemJids.stream().collect(Collectors.toMap(jid -> jid, jid -> map.getOrDefault(jid, 0L)));
    }

    @Override
    public Submission createSubmission(SubmissionData data, ProblemSubmissionConfig config) {
        SM model = submissionDao.createSubmissionModel();
        toModel(data, config, model);
        return submissionFromModels(submissionDao.insert(model), null);
    }

    @Override
    public void updateSubmissionGradingEngine(String submissionJid, String gradingEngine) {
        Optional<SM> maybeModel = submissionDao.selectByJid(submissionJid);
        if (maybeModel.isPresent()) {
            SM model = maybeModel.get();
            model.gradingEngine = gradingEngine;
            submissionDao.update(model);
        }
    }

    @Override
    public String createGrading(Submission submission) {
        GM model = gradingDao.createGradingModel();
        model.submissionJid = submission.getJid();
        model.verdictCode = Verdict.PENDING.getCode();
        model.verdictName = "";
        model.score = 0;

        return gradingDao.insert(model).jid;
    }

    @Override
    public Optional<Submission> updateGrading(String gradingJid, GradingResult result) {
        Optional<GM> maybeModel = gradingDao.selectByJid(gradingJid);
        if (!maybeModel.isPresent()) {
            return Optional.empty();
        }

        GM model = maybeModel.get();
        Optional<SM> submissionModel = submissionDao.selectByJid(model.submissionJid);
        if (!submissionModel.isPresent()) {
            return Optional.empty();
        }

        model.verdictCode = result.getVerdict().getCode();
        model.verdictName = "";
        model.score = result.getScore();
        model.details = result.getDetails();

        Optional<Submission> res = Optional.of(submissionFromModels(submissionModel.get(), model));

        gradingDao.update(model);

        return res;
    }

    private Submission submissionFromModels(SM model, GM gradingModel) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(Optional.ofNullable(model.containerJid).orElse(model.problemJid))
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .latestGrading(gradingFromModel(gradingModel))
                .build();
    }

    private void toModel(SubmissionData data, ProblemSubmissionConfig config, SM model) {
        model.problemJid = data.getProblemJid();
        model.containerJid = data.getContainerJid();
        model.gradingEngine = config.getGradingEngine();
        model.gradingLanguage = data.getGradingLanguage();
    }

    private Optional<Grading> gradingFromModel(@Nullable GM model) {
        if (model == null) {
            return Optional.empty();
        }

        Grading.Builder grading = new Grading.Builder()
                .id(model.id)
                .jid(model.jid)
                .verdict(normalizeVerdict(model.verdictCode, model.verdictName))
                .score(model.score);

        if (model.details != null) {
            GradingResultDetails details;

            try {
                details = mapper.readValue(model.details, GradingResultDetails.class);
            } catch (IOException e) {
                details = new GradingResultDetails.Builder()
                        .errorMessage(model.details)
                        .build();
            }

            grading.details(details);
        }

        return Optional.of(grading.build());
    }

    // Previously, we had OK verdicts with name e.g. "OK (worst: TLE)".
    // These verdicts have been confusing, so in the new version, we replace it to just e.g. TLE.
    // TODO(fushar): Remove this "worst" feature completely, by updating Gabriel grading logic.
    private static Verdict normalizeVerdict(String code, String name) {
        Pattern pattern = Pattern.compile("^.*\\(worst: (.*)\\)$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return Verdicts.fromCode(matcher.group(1));
        }
        return Verdicts.fromCode(code);
    }
}
