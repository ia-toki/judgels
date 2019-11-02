package judgels.jerahmeel.api.chapter.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterSubmissionsResponse.class)
public interface ChapterSubmissionsResponse {
    Page<Submission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableChapterSubmissionsResponse.Builder {}
}
