package judgels.jophiel.profile;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;

@Singleton
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
    public Map<String, Profile> getProfiles(Set<String> userJids) {
        return profileStore.getProfiles(clock.instant(), userJids);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, Profile> getPastProfiles(Set<String> userJids) {
        return profileStore.getProfiles(Instant.ofEpochMilli(0), userJids);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public BasicProfile getBasicProfile(String userJid) {
        return checkFound(profileStore.getBasicProfile(clock.instant(), userJid));
    }
}
