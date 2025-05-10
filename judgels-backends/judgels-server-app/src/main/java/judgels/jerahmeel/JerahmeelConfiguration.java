package judgels.jerahmeel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.jerahmeel.submission.programming.SubmissionConfiguration;
import org.immutables.value.Value;
import tlx.fs.aws.AwsConfiguration;

@Value.Immutable
@JsonDeserialize(as = ImmutableJerahmeelConfiguration.class)
public interface JerahmeelConfiguration {
    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    @JsonProperty("stats")
    StatsConfiguration getStatsConfig();

    class Builder extends ImmutableJerahmeelConfiguration.Builder {}
}
