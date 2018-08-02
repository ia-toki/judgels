package judgels.jophiel.profile;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.info.BasicProfile;
import judgels.jophiel.api.info.Profile;
import judgels.jophiel.api.info.ProfileService;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

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
    public Page<Profile> getTopRatedProfiles(Optional<Integer> page, Optional<Integer> pageSize) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);
        pageSize.ifPresent(options::pageSize);

        return profileStore.getTopRatedProfiles(clock.instant(), options.build());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public BasicProfile getBasicProfile(String userJid) {
        return checkFound(profileStore.getBasicProfile(clock.instant(), userJid));
    }
}
