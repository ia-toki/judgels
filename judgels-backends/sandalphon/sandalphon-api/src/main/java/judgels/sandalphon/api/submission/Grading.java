package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GradingResultDetails;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGrading.class)
public interface Grading {
    long getId();
    String getJid();
    String getVerdict();
    int getScore();
    Optional<GradingResultDetails> getDetails();

    class Builder extends ImmutableGrading.Builder {}
}
