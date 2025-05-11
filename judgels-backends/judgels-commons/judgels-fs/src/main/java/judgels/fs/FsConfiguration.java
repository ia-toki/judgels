package judgels.fs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import judgels.fs.local.LocalFsConfiguration;
import tlx.fs.aws.AwsFsConfiguration;
import tlx.fs.duplex.DuplexFsConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AwsFsConfiguration.class),
        @JsonSubTypes.Type(value = LocalFsConfiguration.class),
        @JsonSubTypes.Type(value = DuplexFsConfiguration.class)})
public interface FsConfiguration {}
