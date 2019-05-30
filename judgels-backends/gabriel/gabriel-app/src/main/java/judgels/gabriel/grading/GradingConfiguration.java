package judgels.gabriel.grading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingConfiguration.class)
public interface GradingConfiguration {
    Optional<String> getLocalSandalphonBaseDataDir();
    int getNumWorkerThreads();

    class Builder extends ImmutableGradingConfiguration.Builder {}
}
