package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.gabriel.api.GradingResult;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.programming.Submission;
import org.iatoki.judgels.play.Page;

import java.util.List;
import java.util.Set;

public interface ProgrammingSubmissionService {
    Submission findProgrammingSubmissionById(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException;

    long countProgrammingSubmissionsByUserJid(String containerJid, String problemJid, String userJid);

    List<Submission> getAllProgrammingSubmissions();

    List<Submission> getProgrammingSubmissionsByJids(List<String> programmingSubmissionJids);

    List<Submission> getProgrammingSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    Page<Submission> getPageOfProgrammingSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    String submit(String problemJid, String containerJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, SubmissionSource submissionSource, String userJid, String userIpAddress) throws ProgrammingSubmissionException;

    void regrade(String submissionJid, SubmissionSource submissionSource, String userJid, String userIpAddress);

    void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress);

    void afterGrade(String gradingJid, GradingResult result);

    boolean gradingExists(String gradingJid);
}
