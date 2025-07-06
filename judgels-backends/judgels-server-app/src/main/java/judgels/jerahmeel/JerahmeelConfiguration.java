package judgels.jerahmeel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.jerahmeel.submission.programming.SubmissionConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJerahmeelConfiguration.class)
public interface JerahmeelConfiguration {
    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("submission")
    Optional<SubmissionConfiguration> getSubmissionConfig();

    @JsonProperty("stats")
    StatsConfiguration getStatsConfig();

    @JsonProperty("aws")
    Optional<judgels.contrib.fs.aws.AwsConfiguration> getAwsConfig();

    class Builder extends ImmutableJerahmeelConfiguration.Builder {}
}
