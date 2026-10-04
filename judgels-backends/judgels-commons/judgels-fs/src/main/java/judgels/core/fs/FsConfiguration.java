package judgels.core.fs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import judgels.core.fs.aws.AwsFsConfiguration;
import judgels.core.fs.local.LocalFsConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AwsFsConfiguration.class),
        @JsonSubTypes.Type(value = LocalFsConfiguration.class)})
public interface FsConfiguration {}
