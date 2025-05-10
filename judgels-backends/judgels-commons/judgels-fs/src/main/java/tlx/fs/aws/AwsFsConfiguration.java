package tlx.fs.aws;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import org.immutables.value.Value;

@JsonTypeName("aws")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAwsFsConfiguration.class)
public interface AwsFsConfiguration extends FsConfiguration {
    String getS3BucketName();

    class Builder extends ImmutableAwsFsConfiguration.Builder {}
}
