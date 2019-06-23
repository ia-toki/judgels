package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.gabriel.api.GradingRequest;
import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.OutputOnlyOverrides;
import judgels.gabriel.api.SubmissionSource;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingModel;
import org.iatoki.judgels.sandalphon.problem.programming.grading.AbstractProgrammingGradingModel_;
import org.iatoki.judgels.sandalphon.problem.programming.grading.BaseProgrammingGradingDao;

import javax.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.time.Instant;
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
    public ProgrammingSubmission findProgrammingSubmissionById(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException {
        SM submissionModel = programmingSubmissionDao.findById(programmingSubmissionId);
        List<GM> gradingModels = programmingGradingDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(AbstractProgrammingGradingModel_.submissionJid, submissionModel.jid), 0, -1);

        return ProgrammingSubmissionServiceUtils.createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public ProgrammingSubmission findProgrammingSubmissionByJid(String programmingSubmissionJid) {
        SM submissionModel = programmingSubmissionDao.findByJid(programmingSubmissionJid);
        List<GM> gradingModels = programmingGradingDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(AbstractProgrammingGradingModel_.submissionJid, submissionModel.jid), 0, -1);

        return ProgrammingSubmissionServiceUtils.createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public long countProgrammingSubmissionsByUserJid(String containerJid, String problemJid, String userJid) {
        return programmingSubmissionDao.countByContainerJidAndUserJidAndProblemJid(containerJid, userJid, problemJid);
    }

    @Override
    public List<Instant> getAllProgrammingSubmissionsSubmitTime() {
        return programmingSubmissionDao.getAllSubmissionsSubmitTime();
    }

    @Override
    public List<ProgrammingSubmission> getAllProgrammingSubmissions() {
        List<SM> submissionModels = programmingSubmissionDao.getAll();
        Map<String, List<GM>> gradingModelsMap = programmingGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJid(String containerJid) {
        List<SM> submissionModels = programmingSubmissionDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(AbstractProgrammingSubmissionModel_.containerJid, containerJid), 0, -1);
        Map<String, GM> gradingModelsMap = programmingGradingDao.getLatestBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModels(m, ImmutableList.of(gradingModelsMap.get(m.jid))));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid) {
        List<SM> submissionModels = programmingSubmissionDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.<SingularAttribute<? super SM, ? extends Object>, String>of(AbstractProgrammingSubmissionModel_.containerJid, containerJid, AbstractProgrammingSubmissionModel_.problemJid, problemJid, AbstractProgrammingSubmissionModel_.createdBy, userJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = programmingGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidBeforeTime(String containerJid, long time) {
        List<SM> submissionModels = programmingSubmissionDao.getByContainerJidSinceTime(containerJid, time);
        Map<String, GM> gradingModelsMap = programmingGradingDao.getLatestBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModels(m, ImmutableList.of(gradingModelsMap.get(m.jid))));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsByJids(List<String> programmingSubmissionJids) {
        List<SM> submissionModels = programmingSubmissionDao.getByJids(programmingSubmissionJids);

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModel(m));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
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

        return Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModel(m));
    }

    @Override
    public Page<ProgrammingSubmission> getPageOfProgrammingSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
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
        Map<String, List<GM>> gradingModelsMap = programmingGradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<ProgrammingSubmission> submissions = Lists.transform(submissionModels, m -> ProgrammingSubmissionServiceUtils.createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page<>(submissions, totalRowsCount, pageIndex, pageSize);
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
}
