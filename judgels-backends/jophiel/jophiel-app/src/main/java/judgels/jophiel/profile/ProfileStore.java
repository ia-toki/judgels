package judgels.jophiel.profile;

import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.info.BasicProfile;
import judgels.jophiel.api.info.Profile;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.jophiel.user.rating.UserRatingStore;
import judgels.jophiel.user.rating.UserWithRating;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

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
                .collect(Collectors.toMap(e -> e.getKey(), e -> new Profile.Builder()
                        .username(e.getValue().getUsername())
                        .nationality(Optional.ofNullable(infos.get(e.getKey())).flatMap(UserInfo::getCountry))
                        .rating(Optional.ofNullable(ratings.get(e.getKey())))
                        .build()));
    }

    public Page<Profile> getTopRatedProfiles(Instant time, SelectionOptions options) {
        Page<UserWithRating> ratings = ratingStore.getTopRatings(time, options);
        Set<String> userJids = ratings.getData().stream().map(UserWithRating::getUserJid).collect(Collectors.toSet());
        Map<String, User> users = userStore.getUsersByJids(userJids);
        Map<String, UserInfo> infos = infoStore.getInfos(userJids);

        return ratings.mapData(data -> data
                .stream()
                .filter(e -> users.containsKey(e.getUserJid()))
                .map(e -> new Profile.Builder()
                        .username(users.get(e.getUserJid()).getUsername())
                        .nationality(Optional.ofNullable(infos.get(e.getUserJid())).flatMap(UserInfo::getCountry))
                        .rating(Optional.of(e.getRating()))
                        .build())
                .collect(Collectors.toList()));
    }

    public Optional<BasicProfile> getBasicProfile(Instant time, String userJid) {
        return userStore.getUserByJid(userJid).map(user -> {
            Map<String, Integer> ratings = ratingStore.getRatings(time, ImmutableSet.of(userJid));
            UserInfo info = infoStore.getInfo(userJid);

            return new BasicProfile.Builder()
                    .username(user.getUsername())
                    .nationality(info.getCountry())
                    .rating(Optional.ofNullable(ratings.get(userJid)))
                    .name(info.getName())
                    .build();
        });
    }
}
