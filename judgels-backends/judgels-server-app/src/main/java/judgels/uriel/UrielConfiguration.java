package judgels.uriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.uriel.file.FileConfiguration;
import judgels.uriel.submission.programming.SubmissionConfiguration;
import org.immutables.value.Value;
import tlx.fs.aws.AwsConfiguration;
import tlx.fs.aws.AwsFsConfiguration;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielConfiguration.class)
public interface UrielConfiguration {
    @JsonProperty("gabriel")
    GabrielClientConfiguration getGabrielConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    @JsonProperty("file")
    FileConfiguration getFileConfig();

    // TLX

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

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
