package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonPermissions.class)
public interface LessonPermissions {
    boolean getCanEdit();

    class Builder extends ImmutableLessonPermissions.Builder {}
}
