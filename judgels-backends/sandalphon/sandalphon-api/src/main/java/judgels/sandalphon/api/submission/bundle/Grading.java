package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGrading.class)
public interface Grading {
    Verdict getVerdict();
    Optional<Double> getScore();

    class Builder extends ImmutableGrading.Builder {}
}
