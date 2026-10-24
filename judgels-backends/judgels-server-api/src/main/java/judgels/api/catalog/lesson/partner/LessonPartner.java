package judgels.api.catalog.lesson.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonPartner.class)
public interface LessonPartner {
    String getUserJid();

    class Builder extends ImmutableLessonPartner.Builder {}
}
