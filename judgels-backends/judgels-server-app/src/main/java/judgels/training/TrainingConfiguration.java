package judgels.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.stats.StatsConfiguration;
import judgels.submission.programming.SubmissionConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingConfiguration.class)
public interface TrainingConfiguration {
    @JsonProperty("aws")
    Optional<judgels.fs.aws.AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    Optional<SubmissionConfiguration> getSubmissionConfig();

    @JsonProperty("stats")
    StatsConfiguration getStatsConfig();

    class Builder extends ImmutableTrainingConfiguration.Builder {}
}
