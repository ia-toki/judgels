package judgels.fs.aws;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAwsConfiguration.class)
public interface AwsConfiguration {
    String getAccessKey();
    String getSecretKey();
    String getS3BucketRegionId();
    String getCloudFrontBaseUrl();

    class Builder extends ImmutableAwsConfiguration.Builder {}
}
