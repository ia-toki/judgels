package judgels.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.Verdict;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGrading.class)
public interface Grading {
    long getId();
    String getJid();
    Verdict getVerdict();
    int getScore();
    Optional<GradingResultDetails> getDetails();

    class Builder extends ImmutableGrading.Builder {}
}
