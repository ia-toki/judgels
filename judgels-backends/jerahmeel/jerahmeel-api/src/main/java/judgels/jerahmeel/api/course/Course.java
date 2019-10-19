package judgels.jerahmeel.api.course;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCourse.class)
public interface Course {
    long getId();
    String getJid();
    String getSlug();
    String getName();
    Optional<String> getDescription();

    class Builder extends ImmutableCourse.Builder {}
}
