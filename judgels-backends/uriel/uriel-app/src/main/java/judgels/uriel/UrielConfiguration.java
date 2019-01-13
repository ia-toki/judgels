package judgels.uriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.fs.aws.AwsFsConfiguration;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.uriel.file.FileConfiguration;
import judgels.uriel.submission.SubmissionConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielConfiguration.class)
public interface UrielConfiguration {
    String getBaseDataDir();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("sandalphon")
    SandalphonClientConfiguration getSandalphonConfig();

    @JsonProperty("sealtiel")
    SealtielClientConfiguration getSealtielConfig();

    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    @JsonProperty("file")
    FileConfiguration getFileConfig();

    @Value.Check
    default void check() {
        if (getSubmissionConfig().getFs() instanceof AwsFsConfiguration && !getAwsConfig().isPresent()) {
            throw new IllegalStateException("aws config is required by submission config");
        }
        if (getFileConfig().getFs() instanceof AwsFsConfiguration && !getAwsConfig().isPresent()) {
            throw new IllegalStateException("aws config is required by file config");
        }
    }

    class Builder extends ImmutableUrielConfiguration.Builder {}
}
