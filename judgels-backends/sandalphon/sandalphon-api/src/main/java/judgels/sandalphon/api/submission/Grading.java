package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.Verdict;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGrading.class)
public interface Grading {
    Verdict PENDING = Verdict.of("?", "Pending");
    Verdict OK = Verdict.of("OK", "Ok");
    Verdict ACCEPTED = Verdict.of("AC", "Accepted");
    Verdict TIME_LIMIT = Verdict.of("TLR", "Time Limit Exceeded");

    long getId();
    String getJid();
    Verdict getVerdict();
    int getScore();
    Optional<GradingResultDetails> getDetails();

    class Builder extends ImmutableGrading.Builder {}
}
