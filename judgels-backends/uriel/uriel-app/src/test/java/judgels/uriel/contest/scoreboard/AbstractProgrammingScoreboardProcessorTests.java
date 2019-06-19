package judgels.uriel.contest.scoreboard;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.SubtaskResult;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import org.assertj.core.util.Lists;

public abstract class AbstractProgrammingScoreboardProcessorTests {
    protected Submission createMilliSubmission(
            long id,
            int time,
            String userJid,
            String problemJid,
            int score,
            Verdict verdict,
            int... subtaskPoints) {

        List<SubtaskResult> subtaskResults = Lists.newArrayList();
        for (int i = 0; i < subtaskPoints.length; i++) {
            subtaskResults.add(new SubtaskResult.Builder()
                    .id(i + 1)
                    .verdict(Verdict.OK)
                    .score(0.0 + subtaskPoints[i])
                    .build());
        }
        Optional<GradingResultDetails> details = Optional.of(new GradingResultDetails.Builder()
                .subtaskResults(subtaskResults)
                .build());

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
                        .details(details)
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
