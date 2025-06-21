package judgels.fs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import judgels.contrib.fs.aws.AwsFsConfiguration;
import judgels.fs.local.LocalFsConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AwsFsConfiguration.class),
        @JsonSubTypes.Type(value = LocalFsConfiguration.class)})
public interface FsConfiguration {}
