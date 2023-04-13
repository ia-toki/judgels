package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.ImmutableBundleStyleModuleConfig;
import judgels.uriel.api.contest.module.ImmutableGcjStyleModuleConfig;
import judgels.uriel.api.contest.module.ImmutableIcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.ImmutableIoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ImmutableTrocStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestStyleDump.class)
public interface ContestStyleDump extends Dump {
    ContestStyle getName();

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "name",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableTrocStyleModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableIoiStyleModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableIcpcStyleModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableGcjStyleModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableBundleStyleModuleConfig.class)
    })
    StyleModuleConfig getConfig();

    class Builder extends ImmutableContestStyleDump.Builder {}
}
