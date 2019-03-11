package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableShortAnswerItemConfig.class)
public interface ShortAnswerItemConfig extends ItemConfig {
    int getScore();
    int getPenalty();
    String getInputValidationRegex();
    Optional<String> getGradingRegex();

    class Builder extends ImmutableShortAnswerItemConfig.Builder {}
}
