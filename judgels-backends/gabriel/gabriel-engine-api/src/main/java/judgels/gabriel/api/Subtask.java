package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubtask.class)
public interface Subtask {
    int getId();
    int getPoints();

    static Subtask of(int id, int points) {
        return new Builder().id(id).points(points).build();
    }

    class Builder extends ImmutableSubtask.Builder {}
}
