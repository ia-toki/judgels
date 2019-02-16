package judgels.sandalphon.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;
import judgels.sandalphon.persistence.BaseProgrammingGradingDao;
import judgels.sandalphon.persistence.BaseProgrammingSubmissionDao;

public abstract class AbstractSubmissionStore<
        SM extends AbstractProgrammingSubmissionModel, GM extends AbstractProgrammingGradingModel> {

    private static final int MAX_DOWNLOAD_SUBMISSIONS_LIMIT = 100;

    private final BaseProgrammingSubmissionDao<SM> submissionDao;
    private final BaseProgrammingGradingDao<GM> gradingDao;
    private final ObjectMapper mapper;

    public AbstractSubmissionStore(
            BaseProgrammingSubmissionDao<SM> submissionDao,
            BaseProgrammingGradingDao<GM> gradingDao,
            ObjectMapper mapper) {

        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.mapper = mapper;
    }

    public Optional<Submission> getSubmissionById(long submissionId) {
        return submissionDao.select(submissionId).map(model -> {
            Optional<GM> gradingModel = gradingDao.selectLatestBySubmissionJid(model.jid);
            return submissionFromModels(model, gradingModel.orElse(null));
        });
    }

    public Page<Submission> getSubmissionsForDownload(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            Optional<Integer> limit) {

        SelectionOptions options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .pageSize(Math.min(MAX_DOWNLOAD_SUBMISSIONS_LIMIT, limit.orElse(MAX_DOWNLOAD_SUBMISSIONS_LIMIT)))
                .orderDir(OrderDir.ASC)
                .build();

        return getSubmissions(containerJid, userJid, problemJid, lastSubmissionId, options);
    }

    public Page<Submission> getSubmissions(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        return getSubmissions(containerJid, userJid, problemJid, Optional.empty(), options.build());
    }

    private Page<Submission> getSubmissions(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options) {

        Page<SM> submissionModels =
                submissionDao.selectPaged(containerJid, userJid, problemJid, lastSubmissionId, options);
        Set<String> submissionJids = submissionModels.getPage().stream().map(m -> m.jid).collect(Collectors.toSet());
        Map<String, GM> gradingModels = gradingDao.selectAllLatestBySubmissionJids(submissionJids);

        return submissionModels.mapPage(p ->
                Lists.transform(p, sm -> submissionFromModels(sm, gradingModels.get(sm.jid))));
    }

    public long getTotalSubmissions(String containerJid, String userJid, String problemJid) {
        Map<String, Long> map = submissionDao.selectCounts(containerJid, userJid, ImmutableSet.of(problemJid));
        return map.getOrDefault(problemJid, 0L);
    }

    public Map<String, Long> getTotalSubmissionsMap(String containerJid, String userJid, Set<String> problemJids) {
        Map<String, Long> map = submissionDao.selectCounts(containerJid, userJid, problemJids);
        return problemJids.stream().collect(Collectors.toMap(jid -> jid, jid -> map.getOrDefault(jid, 0L)));
    }

    public Submission createSubmission(SubmissionData data, String gradingEngine) {
        SM model = submissionDao.createSubmissionModel();
        toModel(data, gradingEngine, model);
        return submissionFromModels(submissionDao.insert(model), null);
    }

    public String createGrading(Submission submission) {
        GM model = gradingDao.createGradingModel();
        model.submissionJid = submission.getJid();
        model.verdictCode = Verdicts.PENDING.getCode();
        model.verdictName = Verdicts.PENDING.getName();
        model.score = 0;

        return gradingDao.insert(model).jid;
    }

    private Submission submissionFromModels(SM model, GM gradingModel) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(model.containerJid)
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .latestGrading(gradingFromModel(gradingModel))
                .build();
    }

    private void toModel(SubmissionData data, String gradingEngine, SM model) {
        model.problemJid = data.getProblemJid();
        model.containerJid = data.getContainerJid();
        model.gradingEngine = gradingEngine;
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
            RawGradingResultDetails raw;
            try {
                raw = mapper.readValue(model.details, RawGradingResultDetails.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<String, byte[]> compilationOutputs = raw.getCompilationOutputs().entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getBytes()));

            grading.details(new GradingResultDetails.Builder()
                    .compilationOutputs(compilationOutputs)
                    .testDataResults(raw.getTestDataResults())
                    .subtaskResults(raw.getSubtaskResults())
                    .build());
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
            return Verdict.of(matcher.group(1), "");
        }
        return Verdict.of(code, name);
    }
}
