package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemItem.class)
public interface ProblemItem {
    String getJid();
    ProblemItemType getType();
    String getMeta();
    String getConfig();

    class Builder extends ImmutableProblemItem.Builder {}
}
