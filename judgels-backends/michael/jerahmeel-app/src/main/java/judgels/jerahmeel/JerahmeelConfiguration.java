package judgels.jerahmeel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.jerahmeel.submission.programming.SubmissionConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.uriel.api.UrielClientConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJerahmeelConfiguration.class)
public interface JerahmeelConfiguration {
    String getBaseDataDir();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("sandalphon")
    SandalphonClientConfiguration getSandalphonConfig();

    @JsonProperty("uriel")
    Optional<UrielClientConfiguration> getUrielConfig();

    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    @JsonProperty("stats")
    StatsConfiguration getStatsConfig();

    class Builder extends ImmutableJerahmeelConfiguration.Builder {}
}
