package judgels.uriel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.uriel.jophiel.JophielConfiguration;
import judgels.uriel.submission.SubmissionConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUrielConfiguration.class)
public interface UrielConfiguration {
    @JsonProperty("jophiel")
    JophielConfiguration getJophielConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    class Builder extends ImmutableUrielConfiguration.Builder {}
}
