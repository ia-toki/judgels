package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubtaskResult.class)
public interface SubtaskResult {
    int getId();
    Verdict getVerdict();
    double getScore();

    class Builder extends ImmutableSubtaskResult.Builder {}
}
