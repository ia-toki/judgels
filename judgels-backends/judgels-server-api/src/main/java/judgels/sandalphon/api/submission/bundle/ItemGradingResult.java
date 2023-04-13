package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemGradingResult.class)
public interface ItemGradingResult {
    int getNumber();
    double getScore();

    class Builder extends ImmutableItemGradingResult.Builder {}
}
