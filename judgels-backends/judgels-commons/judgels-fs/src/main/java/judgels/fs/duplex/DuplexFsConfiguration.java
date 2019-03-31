package judgels.fs.duplex;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import judgels.fs.aws.AwsFsConfiguration;
import org.immutables.value.Value;

@JsonTypeName("duplex")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableDuplexFsConfiguration.class)
public interface DuplexFsConfiguration extends FsConfiguration {
    String getS3BucketName();

    default AwsFsConfiguration toAwsFsConfig() {
        return new AwsFsConfiguration.Builder()
                .s3BucketName(getS3BucketName())
                .build();
    }

    class Builder extends ImmutableDuplexFsConfiguration.Builder {}
}
