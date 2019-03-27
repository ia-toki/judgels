package judgels.uriel.api.dump;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import java.util.Optional;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ImmutableClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ImmutableFrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ImmutableScoreboardModuleConfig;
import judgels.uriel.api.contest.module.ImmutableVirtualModuleConfig;
import judgels.uriel.api.contest.module.ModuleConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestModuleDump.class)
public interface ContestModuleDump extends ContestManager, Dump {
    ContestModuleType getName();
    boolean getEnabled();

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "name",
            visible = true,
            defaultImpl = NoClass.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableScoreboardModuleConfig.class, name = "SCOREBOARD"),
            @JsonSubTypes.Type(value = ImmutableClarificationTimeLimitModuleConfig.class,
                    name = "CLARIFICATION_TIME_LIMIT"),
            @JsonSubTypes.Type(value = ImmutableFrozenScoreboardModuleConfig.class, name = "FROZEN_SCOREBOARD"),
            @JsonSubTypes.Type(value = ImmutableVirtualModuleConfig.class, name = "VIRTUAL")
    })
    Optional<ModuleConfig> getConfig();

    class Builder extends ImmutableContestModuleDump.Builder {}
}
