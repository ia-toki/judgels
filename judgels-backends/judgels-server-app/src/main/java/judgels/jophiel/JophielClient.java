package judgels.jophiel;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.RatingEvent;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.rating.UserRatingStore;

public class JophielClient {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\\[user:(\\S+)]");

    @Inject protected UserStore userStore;
    @Inject protected UserRatingStore userRatingStore;
    @Inject protected ProfileStore profileStore;

    @Inject public JophielClient() {}

    public Optional<String> translateUsernameToJid(String username) {
        return userStore.translateUsernameToJid(username);
    }

    public Map<String, String> translateUsernamesToJids(Set<String> usernames) {
        return userStore.translateUsernamesToJids(usernames);
    }

    public Profile getProfile(String userJid) {
        return profileStore.getProfile(userJid);
    }

    public Profile getProfile(String userJid, Instant time) {
        return profileStore.getProfile(userJid, time);
    }

    public Map<String, Profile> getProfiles(Set<String> userJids) {
        return profileStore.getProfiles(userJids);
    }

    public Map<String, Profile> getProfiles(Set<String> userJids, Instant time) {
        return profileStore.getProfiles(userJids, time);
    }

    public Map<String, Profile> parseProfiles(String str) {
        Set<String> usernames = Sets.newHashSet();
        Matcher m = USERNAME_PATTERN.matcher(str);
        while (m.find()) {
            usernames.add(m.group(1));
        }
        return getProfiles(ImmutableSet.copyOf(translateUsernamesToJids(usernames).values()));
    }

    public List<UserRatingEvent> getUserRatingEvents(String userJid) {
        return userRatingStore.getUserRatingEvents(userJid);
    }

    public Optional<RatingEvent> getLatestRatingEvent() {
        return userRatingStore.getLatestRatingEvent();
    }
}
