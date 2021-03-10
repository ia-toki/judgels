package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetsResponse.class)
public interface ProblemSetsResponse {
    Page<ProblemSet> getData();
    Map<String, String> getArchiveSlugsMap();
    Map<String, String> getArchiveDescriptionsMap();
    Optional<String> getArchiveName();
    Map<String, ProblemSetProgress> getProblemSetProgressesMap();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemSetsResponse.Builder {}
}
