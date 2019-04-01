package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import javax.annotation.Nullable;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ImmutableClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ImmutableFrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ImmutableScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ImmutableVirtualModuleConfig;
import judgels.uriel.api.contest.module.ModuleConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestModuleDump.class)
public abstract class ContestModuleDump implements Dump {
    public abstract ContestModuleType getName();
    public abstract boolean getEnabled();

    @Nullable
    @Value.Default
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "name",
            visible = true,
            defaultImpl = NoClass.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableScoreboardModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableClarificationTimeLimitModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableFrozenScoreboardModuleConfig.class),
            @JsonSubTypes.Type(value = ImmutableVirtualModuleConfig.class)
    })
    public ModuleConfig getConfig() {
        return null;
    }

    public static class Builder extends ImmutableContestModuleDump.Builder {}
}
