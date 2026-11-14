package judgels.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.core.fs.aws.AwsConfiguration;
import judgels.submission.programming.SubmissionConfiguration;
import judgels.training.stats.StatsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingConfiguration.class)
public interface TrainingConfiguration {
    @Value.Default
    default boolean getEnabled() {
        return false;
    }

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    Optional<SubmissionConfiguration> getSubmissionConfig();

    @JsonProperty("stats")
    @Value.Default
    default StatsConfiguration getStatsConfig() {
        return StatsConfiguration.DEFAULT;
    }

    class Builder extends ImmutableTrainingConfiguration.Builder {}
}
