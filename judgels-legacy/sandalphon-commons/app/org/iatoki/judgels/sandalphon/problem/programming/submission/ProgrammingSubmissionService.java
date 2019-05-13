package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.SubmissionSource;
import org.iatoki.judgels.play.Page;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface ProgrammingSubmissionService {

    ProgrammingSubmission findProgrammingSubmissionById(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException;

    ProgrammingSubmission findProgrammingSubmissionByJid(String programmingSubmissionJid);

    long countProgrammingSubmissionsByUserJid(String containerJid, String problemJid, String userJid);

    List<Instant> getAllProgrammingSubmissionsSubmitTime();

    List<ProgrammingSubmission> getAllProgrammingSubmissions();

    List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJid(String containerJid);

    List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid);

    List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidBeforeTime(String containerJid, long time);

    List<ProgrammingSubmission> getProgrammingSubmissionsByJids(List<String> programmingSubmissionJids);

    List<ProgrammingSubmission> getProgrammingSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    Page<ProgrammingSubmission> getPageOfProgrammingSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    String submit(String problemJid, String containerJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, SubmissionSource submissionSource, String userJid, String userIpAddress) throws ProgrammingSubmissionException;

    void regrade(String submissionJid, SubmissionSource submissionSource, String userJid, String userIpAddress);

    void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress);

    void afterGrade(String gradingJid, GradingResult result);

    boolean gradingExists(String gradingJid);
}
