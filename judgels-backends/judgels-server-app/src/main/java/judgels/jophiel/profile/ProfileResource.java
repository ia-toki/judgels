package judgels.jophiel.profile;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;

public class ProfileResource implements ProfileService {
    private final Clock clock;
    private final ProfileStore profileStore;

    @Inject
    public ProfileResource(Clock clock, ProfileStore profileStore) {
        this.clock = clock;
        this.profileStore = profileStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, Profile> getProfiles(Set<String> userJids, Optional<Long> time) {
        return profileStore.getProfiles(userJids, time.map(Instant::ofEpochMilli).orElse(clock.instant()));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<Profile> getTopRatedProfiles(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        return profileStore.getTopRatedProfiles(clock.instant(), pageNumber.orElse(1), pageSize.orElse(50));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public BasicProfile getBasicProfile(String userJid) {
        return checkFound(profileStore.getBasicProfile(clock.instant(), userJid));
    }
}
