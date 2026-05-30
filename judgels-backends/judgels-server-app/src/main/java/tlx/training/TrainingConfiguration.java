package tlx.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.submission.programming.SubmissionConfiguration;
import org.immutables.value.Value;
import tlx.stats.StatsConfiguration;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingConfiguration.class)
public interface TrainingConfiguration {
    @JsonProperty("stats")
    StatsConfiguration getStatsConfig();

    @JsonProperty("submission")
    Optional<SubmissionConfiguration> getSubmissionConfig();

    @JsonProperty("aws")
    Optional<tlx.fs.aws.AwsConfiguration> getAwsConfig();

    class Builder extends ImmutableTrainingConfiguration.Builder {}
}
