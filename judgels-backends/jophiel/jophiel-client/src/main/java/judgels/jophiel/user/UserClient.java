package judgels.jophiel.user;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;

public class UserClient {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\\[user:(\\S+)]");

    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    private final LoadingCache<String, String> usernameToJidCache;
    private final LoadingCache<String, Profile> profileCache;

    @Inject
    public UserClient(UserSearchService userSearchService, ProfileService profileService) {
        this.userSearchService = userSearchService;
        this.profileService = profileService;
        this.usernameToJidCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build(new UsernameToJidCacheLoader());
        this.profileCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build(new ProfileCacheLoader());
    }

    public Optional<String> translateUsernameToJid(String username) {
        return Optional.ofNullable(usernameToJidCache.get(username));
    }

    public Map<String, String> translateUsernamesToJids(Set<String> usernames) {
        return usernameToJidCache.getAll(usernames);
    }

    public Profile getProfile(String userJid) {
        return profileCache.get(userJid);
    }

    public Profile getProfile(String userJid, Instant time) {
        return profileCache.get(userJid + " " + time.toEpochMilli());
    }

    public Map<String, Profile> getProfiles(Set<String> userJids) {
        return profileCache.getAll(userJids);
    }

    public Map<String, Profile> getProfiles(Set<String> userJids, Instant time) {
        Set<String> userJidWithTimes = userJids.stream()
                .map(userJid -> userJid + " " + time.toEpochMilli())
                .collect(Collectors.toSet());
        return profileCache.getAll(userJidWithTimes).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().split(" ")[0], e -> e.getValue()));
    }

    public Map<String, Profile> parseProfiles(String str) {
        Set<String> usernames = Sets.newHashSet();
        Matcher m = USERNAME_PATTERN.matcher(str);
        while (m.find()) {
            usernames.add(m.group(1));
        }
        return getProfiles(ImmutableSet.copyOf(translateUsernamesToJids(usernames).values()));
    }

    private class UsernameToJidCacheLoader implements CacheLoader<String, String> {
        @Nullable
        @Override
        public String load(@Nonnull String userJid) {
            return userSearchService.translateUsernamesToJids(ImmutableSet.of(userJid)).get(userJid);
        }

        @Nonnull
        @Override
        public Map<String, String> loadAll(@Nonnull Iterable<? extends String> userJids) {
            return userSearchService.translateUsernamesToJids(ImmutableSet.copyOf(userJids));
        }
    }

    private class ProfileCacheLoader implements CacheLoader<String, Profile> {
        @Nullable
        @Override
        public Profile load(@Nonnull String userJidWithTime) {
            return loadAll(ImmutableSet.of(userJidWithTime)).get(userJidWithTime);
        }

        @Nonnull
        @Override
        public Map<String, Profile> loadAll(@Nonnull Iterable<? extends String> userJidWithTimes) {
            ImmutableSet.Builder<String> userJids = ImmutableSet.builder();
            Long time = null;

            for (String userJidWithTime : userJidWithTimes) {
                String[] tokens = userJidWithTime.split(" ");
                userJids.add(tokens[0]);
                if (tokens.length > 1) {
                    time = Long.parseLong(tokens[1]);
                }
            }
            Map<String, Profile> profiles = profileService.getProfiles(userJids.build(), Optional.ofNullable(time));

            ImmutableMap.Builder<String, Profile> finalProfiles = ImmutableMap.builder();
            for (Map.Entry<String, Profile> e : profiles.entrySet()) {
                if (time == null) {
                    finalProfiles.put(e);
                } else {
                    finalProfiles.put(e.getKey() + " " + time, e.getValue());
                }
            }
            return finalProfiles.build();
        }
    }
}
