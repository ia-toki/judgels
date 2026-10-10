package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonResponse.class)
public interface LessonResponse {
    Lesson getData();
    boolean getHasLocalChanges();
    LessonConfig getConfig();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableLessonResponse.Builder {}
}
