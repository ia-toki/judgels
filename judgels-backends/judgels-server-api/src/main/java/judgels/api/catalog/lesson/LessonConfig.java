package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonConfig.class)
public interface LessonConfig {
    boolean getCanEdit();

    class Builder extends ImmutableLessonConfig.Builder {}
}
