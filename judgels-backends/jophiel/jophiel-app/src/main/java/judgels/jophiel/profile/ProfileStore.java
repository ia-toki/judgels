package judgels.jophiel.profile;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.jophiel.user.rating.UserRatingStore;

@Singleton
public class ProfileStore {
    private final UserStore userStore;
    private final UserInfoStore infoStore;
    private final UserRatingStore ratingStore;

    @Inject
    public ProfileStore(UserStore userStore, UserInfoStore infoStore, UserRatingStore ratingStore) {
        this.userStore = userStore;
        this.infoStore = infoStore;
        this.ratingStore = ratingStore;
    }

    public Map<String, Profile> getProfiles(Instant time, Set<String> userJids) {
        Map<String, UserInfo> infos = infoStore.getInfos(userJids);
        Map<String, Integer> ratings = ratingStore.getRatings(time, userJids);

        return userStore.getUsersByJids(userJids).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new Profile.Builder()
                                .username(e.getValue().getUsername())
                                .nationality(
                                        Optional.ofNullable(infos.get(e.getKey())).flatMap(UserInfo::getNationality))
                                .rating(
                                        Optional.ofNullable(ratings.get(e.getKey())))
                                .build()));
    }

    public Optional<BasicProfile> getBasicProfile(Instant time, String userJid) {
        return userStore.getUserByJid(userJid).map(user -> {
            return new BasicProfile.Builder()
                    .username(user.getUsername())
                    .build();
        });
    }
}
