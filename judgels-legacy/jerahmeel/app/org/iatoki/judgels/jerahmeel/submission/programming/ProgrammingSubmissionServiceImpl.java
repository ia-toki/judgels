package org.iatoki.judgels.jerahmeel.submission.programming;

import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.jerahmeel.chapter.ChapterProgressCacheUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingDao;
import org.iatoki.judgels.jerahmeel.grading.programming.ProgrammingGradingModel;
import org.iatoki.judgels.jerahmeel.jid.JerahmeelJidUtils;
import org.iatoki.judgels.jerahmeel.scorecache.ContainerProblemScoreCacheDao;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.submission.JerahmeelSubmissionServiceUtils;
import org.iatoki.judgels.sandalphon.problem.programming.submission.AbstractProgrammingSubmissionServiceImpl;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ProgrammingSubmissionServiceImpl extends AbstractProgrammingSubmissionServiceImpl<ProgrammingSubmissionModel, ProgrammingGradingModel> implements ProgrammingSubmissionService {
    private final ContainerProblemScoreCacheDao containerProblemScoreCacheDao;
    private final ProgrammingSubmissionDao programmingSubmissionDao;
    private final ProgrammingGradingDao programmingGradingDao;

    @Inject
    public ProgrammingSubmissionServiceImpl(ContainerProblemScoreCacheDao containerProblemScoreCacheDao, ProgrammingSubmissionDao programmingSubmissionDao, ProgrammingGradingDao programmingGradingDao, @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader, MessageService messageService, @Named("gabrielClientJid") String gabrielClientJid) {
        super(programmingSubmissionDao, programmingGradingDao, sealtielClientAuthHeader, messageService, gabrielClientJid);
        this.containerProblemScoreCacheDao = containerProblemScoreCacheDao;
        this.programmingSubmissionDao = programmingSubmissionDao;
        this.programmingGradingDao = programmingGradingDao;
    }

    @Override
    public void afterGrade(String gradingJid, GradingResult result) {
        ProgrammingGradingModel programmingGradingModel = programmingGradingDao.findByJid(gradingJid);
        ProgrammingSubmissionModel programmingSubmissionModel = programmingSubmissionDao.findByJid(programmingGradingModel.submissionJid);

        String userJid = programmingSubmissionModel.createdBy;
        String containerJid = programmingSubmissionModel.containerJid;
        String problemJid = programmingSubmissionModel.problemJid;

        ProgrammingSubmission gradedProgrammingSubmission = this.findProgrammingSubmissionByJid(programmingSubmissionModel.jid);

        List<ProgrammingSubmission> submissions = this.getProgrammingSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(containerJid, problemJid, userJid);
        List<ProgrammingSubmission> submissionsWithoutCurrent = submissions.stream().filter(s -> !s.getJid().equals(programmingSubmissionModel.jid)).collect(Collectors.toList());

        double newScore = JerahmeelSubmissionServiceUtils.countProgrammingSubmissionsMaxScore(submissions);
        if (containerProblemScoreCacheDao.existsByUserJidContainerJidAndProblemJid(userJid, containerJid, problemJid)) {
            JerahmeelSubmissionServiceUtils.updateContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        } else {
            JerahmeelSubmissionServiceUtils.createContainerProblemScoreCache(containerProblemScoreCacheDao, userJid, containerJid, problemJid, newScore);
        }

        double previousScore = JerahmeelSubmissionServiceUtils.countProgrammingSubmissionsMaxScore(submissionsWithoutCurrent);
        if (gradedProgrammingSubmission.getGradings().size() > 1) {
            previousScore = Math.max(previousScore, gradedProgrammingSubmission.getGradings().get(gradedProgrammingSubmission.getGradings().size() - 2).getScore());
        }
        boolean scoreChanged = previousScore != newScore;
        double deltaScore = newScore - previousScore;

        if (containerJid.startsWith(JerahmeelJidUtils.PROBLEM_SET_JID_PREFIX)) {
            if (scoreChanged) {
                ProblemSetScoreCacheUtils.getInstance().updateProblemSetAndArchivesScoreCache(userJid, containerJid, deltaScore);
            }
            return;
        }

        if (scoreChanged) {
            ChapterScoreCacheUtils.getInstance().updateChapterAndCourseScoreCache(userJid, containerJid, deltaScore);
        }

        ChapterProgressCacheUtils.getInstance().updateChapterProblemProgressWithProgrammingSubmissions(userJid, containerJid, problemJid, submissions);
    }
}
