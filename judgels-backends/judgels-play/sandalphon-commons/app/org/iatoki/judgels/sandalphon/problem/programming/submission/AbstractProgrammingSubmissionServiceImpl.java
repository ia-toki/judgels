package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.palantir.conjure.java.api.errors.RemoteException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import judgels.gabriel.api.GradingRequest;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.OutputOnlyOverrides;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.persistence.AbstractProgrammingGradingModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel;
import judgels.sandalphon.persistence.AbstractProgrammingSubmissionModel_;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.grading.BaseProgrammingGradingDao;

import javax.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractProgrammingSubmissionServiceImpl<SM extends AbstractProgrammingSubmissionModel, GM extends AbstractProgrammingGradingModel> implements ProgrammingSubmissionService {
    protected static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    private final BaseProgrammingSubmissionDao<SM> programmingSubmissionDao;
    private final BaseProgrammingGradingDao<GM> programmingGradingDao;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final String gabrielClientJid;

    protected AbstractProgrammingSubmissionServiceImpl(BaseProgrammingSubmissionDao<SM> programmingSubmissionDao, BaseProgrammingGradingDao<GM> programmingGradingDao, BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, String gabrielClientJid) {
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.gabrielClientJid = gabrielClientJid;
    }

    @Override
    public Submission findProgrammingSubmissionById(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException {
        SM submissionModel = programmingSubmissionDao.findById(programmingSubmissionId);
        Map<String, GM> gradingModelsMap = programmingGradingDao.getLatestBySubmissionJids(ImmutableList.of(submissionModel.jid));

        return createSubmissionFromModels(submissionModel, gradingModelsMap.get(submissionModel.jid));
    }

    @Override
    public long countProgrammingSubmissionsByUserJid(String containerJid, String problemJid, String userJid) {
        return programmingSubmissionDao.countByContainerJidAndUserJidAndProblemJid(containerJid, userJid, problemJid);
    }

    @Override
    public List<Submission> getAllProgrammingSubmissions() {
        List<SM> submissionModels = programmingSubmissionDao.getAll();
        Map<String, GM> gradingModelsMap = programmingGradingDao.getLatestBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<Submission> getProgrammingSubmissionsByJids(List<String> programmingSubmissionJids) {
        List<SM> submissionModels = programmingSubmissionDao.getByJids(programmingSubmissionJids);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public List<Submission> getProgrammingSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, ? extends Object>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, ? extends Object>, String> filterColumns = filterColumnsBuilder.build();

        List<SM> submissionModels = programmingSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, 0, -1);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public Page<Submission> getPageOfProgrammingSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, ? extends Object>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.createdBy, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, ? extends Object>, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = programmingSubmissionDao.countByFiltersEq("", filterColumns);
        List<SM> submissionModels = programmingSubmissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, pageIndex * pageSize, pageSize);
        Map<String, GM> gradingModelsMap = programmingGradingDao.getLatestBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<Submission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page.Builder<Submission>()
                .page(submissions)
                .totalCount(totalRowsCount)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public final String submit(String problemJid, String containerJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, SubmissionSource submissionSource, String userJid, String userIpAddress) throws ProgrammingSubmissionException {
        if (!gradingLanguage.startsWith(OutputOnlyOverrides.KEY) && allowedLanguageNames != null && !allowedLanguageNames.contains(gradingLanguage)) {
            throw new ProgrammingSubmissionException("Language " + gradingLanguage + " is not allowed ");
        }

        SM submissionModel = programmingSubmissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.containerJid = containerJid;
        submissionModel.gradingEngine = gradingEngine;
        submissionModel.gradingLanguage = gradingLanguage;

        programmingSubmissionDao.persist(submissionModel, userJid, userIpAddress);

        requestGrading(submissionModel, submissionSource, userJid, userIpAddress);

        return submissionModel.jid;
    }

    @Override
    public final void regrade(String submissionJid, SubmissionSource submissionSource, String userJid, String userIpAddress) {
        SM submissionModel = programmingSubmissionDao.findByJid(submissionJid);

        requestGrading(submissionModel, submissionSource, userJid, userIpAddress);
    }

    @Override
    public final void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress) {
        GM gradingModel = programmingGradingDao.findByJid(gradingJid);

        gradingModel.verdictCode = result.getVerdict().getCode();
        gradingModel.verdictName = "";
        gradingModel.score = result.getScore();
        gradingModel.details = result.getDetails();

        programmingGradingDao.edit(gradingModel, grader, graderIpAddress);

        afterGrade(gradingJid, result);
    }

    @Override
    public void afterGrade(String gradingJid, GradingResult result) {
        // To be overridden if needed
    }

    @Override
    public boolean gradingExists(String gradingJid) {
        return programmingGradingDao.existsByJid(gradingJid);
    }

    private void requestGrading(SM submissionModel, SubmissionSource submissionSource, String userJid, String userIpAddress) {
        GM gradingModel = programmingGradingDao.createGradingModel();

        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.verdictCode = "?";
        gradingModel.verdictName = "Pending";
        gradingModel.score = 0;

        programmingGradingDao.persist(gradingModel, userJid, userIpAddress);

        GradingRequest request = new GradingRequest.Builder()
                .gradingJid(gradingModel.jid)
                .problemJid(submissionModel.problemJid)
                .gradingEngine(submissionModel.gradingEngine)
                .gradingLanguage(submissionModel.gradingLanguage)
                .submissionSource(submissionSource)
                .build();

        try {
            MessageData message = new MessageData.Builder()
                    .targetJid(gabrielClientJid)
                    .type(request.getClass().getSimpleName())
                    .content(MAPPER.writeValueAsString(request))
                    .build();
            messageService.sendMessage(sealtielClientAuthHeader, message);
        } catch (RemoteException | IOException e) {
            // log later
        }
    }

    public Submission createSubmissionFromModel(AbstractProgrammingSubmissionModel submissionModel) {
        return createSubmissionFromModels(submissionModel, null);
    }

    private Submission createSubmissionFromModels(AbstractProgrammingSubmissionModel model, AbstractProgrammingGradingModel gradingModel) {
        return new Submission.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .problemJid(model.problemJid)
                .containerJid(model.problemJid)
                .gradingEngine(model.gradingEngine)
                .gradingLanguage(model.gradingLanguage)
                .time(model.createdAt)
                .latestGrading(createGradingFromModel(gradingModel))
                .build();
    }

    private Optional<Grading> createGradingFromModel(@Nullable AbstractProgrammingGradingModel model) {
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
                details = MAPPER.readValue(model.details, GradingResultDetails.class);
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
