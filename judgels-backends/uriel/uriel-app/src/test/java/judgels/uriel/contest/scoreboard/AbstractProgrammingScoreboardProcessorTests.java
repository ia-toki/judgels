package judgels.uriel.contest.scoreboard;

import java.time.Instant;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;

public abstract class AbstractProgrammingScoreboardProcessorTests {
    protected Submission createMilliSubmission(
            long id,
            int time,
            String userJid,
            String problemJid,
            int score,
            Verdict verdict) {

        return new Submission.Builder()
                .containerJid("JIDC")
                .id(id)
                .jid("JIDS" + id)
                .gradingEngine("ENG")
                .gradingLanguage("ASM")
                .time(Instant.ofEpochMilli(time))
                .userJid(userJid)
                .problemJid(problemJid)
                .latestGrading(new Grading.Builder()
                        .id(1)
                        .jid("JIDG-2")
                        .score(score)
                        .verdict(verdict)
                        .build())
                .build();
    }

    protected Submission createSubmission(
            long id,
            int time,
            String userJid,
            String problemJid,
            int score,
            Verdict verdict) {

        return createMilliSubmission(id, time * 1000, userJid, problemJid, score, verdict);
    }
}
