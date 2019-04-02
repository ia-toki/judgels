package judgels.fs.local;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import org.immutables.value.Value;

@JsonTypeName("local")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableLocalFsConfiguration.class)
public interface LocalFsConfiguration extends FsConfiguration {
    class Builder extends ImmutableLocalFsConfiguration.Builder {}
}
