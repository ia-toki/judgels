package judgels;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsAppConfiguration.class)
public interface JudgelsAppConfiguration {
    String getName();
}
