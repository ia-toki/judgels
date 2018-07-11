package judgels.fs.aws;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAwsFsConfiguration.class)
public interface AwsFsConfiguration extends FsConfiguration {
    String getS3BucketName();

    class Builder extends ImmutableAwsFsConfiguration.Builder {}
}
