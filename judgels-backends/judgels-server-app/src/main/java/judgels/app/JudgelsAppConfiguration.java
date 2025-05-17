package judgels.app;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsAppConfiguration.class)
public interface JudgelsAppConfiguration {
    Optional<String> getLicenseKey();
    String getName();

    class Builder extends ImmutableJudgelsAppConfiguration.Builder {}
}
