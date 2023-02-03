package judgels.jerahmeel.api.curriculum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCurriculum.class)
public interface Curriculum {
    String getName();
    Optional<String> getDescription();

    class Builder extends ImmutableCurriculum.Builder {}
}
