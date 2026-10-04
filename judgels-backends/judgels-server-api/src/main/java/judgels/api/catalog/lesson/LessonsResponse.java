package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonsResponse.class)
public interface LessonsResponse {
    Page<Lesson> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableLessonsResponse.Builder {}
}
