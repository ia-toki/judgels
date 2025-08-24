package judgels.jophiel.user;

import jakarta.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.info.UserInfoStore;
import liquibase.util.csv.CSVReader;

public class UserCreator {
    public static class UpsertUsersResult {
        public Optional<String> errorMessage = Optional.empty();
        public List<String> createdUsernames = List.of();
        public List<String> updatedUsernames = List.of();
    }

    private final UserStore userStore;
    private final UserInfoStore infoStore;
    private final SessionStore sessionStore;

    @Inject
    public UserCreator(UserStore userStore, UserInfoStore infoStore, SessionStore sessionStore) {
        this.userStore = userStore;
        this.infoStore = infoStore;
        this.sessionStore = sessionStore;
    }

    public UpsertUsersResult upsertUsers(String csv) throws IOException {
        UpsertUsersResult result = new UpsertUsersResult();

        CSVReader reader = new CSVReader(new StringReader(csv));
        String[] header = reader.readNext();
        if (header == null) {
            result.errorMessage = Optional.of("Missing headers.");
            return result;
        }

        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            headerMap.put(header[i], i);
        }


        for (String optionalHeader : headerMap.keySet()) {
            if (!Set.of("jid", "username", "password", "email", "name", "country", "institution_name", "institution_city", "institution_province").contains(optionalHeader)) {
                result.errorMessage = Optional.of("'" + optionalHeader + "' header is not allowed.");
                return result;
            }
        }

        if (!headerMap.containsKey("jid") && !headerMap.containsKey("username")) {
            result.errorMessage = Optional.of("Either 'jid' or 'username' header (or both) must be present.");
            return result;
        }

        result.createdUsernames = new ArrayList<>();
        result.updatedUsernames = new ArrayList<>();

        while (true) {
            String[] line = reader.readNext();
            if (line == null) {
                break;
            }

            User user;
            Optional<User> existingUser;
            UserInfo existingInfo;
            Optional<String> jid = getCsvValue(headerMap, line, "jid");
            Optional<String> username = getCsvValue(headerMap, line, "username");
            Optional<String> password = getCsvValue(headerMap, line, "password");
            Optional<String> email = getCsvValue(headerMap, line, "email");

            if (jid.isPresent()) {
                existingUser = userStore.getUserByJid(jid.get());
            } else {
                existingUser = userStore.getUserByUsername(username.get());
            }

            if (existingUser.isPresent()) {
                UserUpdateData data = new UserUpdateData.Builder()
                        .username(username)
                        .password(password)
                        .email(email)
                        .build();

                user = userStore.updateUser(existingUser.get().getJid(), data);
                result.updatedUsernames.add(user.getUsername());
                existingInfo = infoStore.getInfo(user.getJid());
            } else {
                String key = jid.orElse(username.orElse(""));

                if (!username.isPresent()) {
                    result.errorMessage = Optional.of("User '" + key + "' not found but cannot be created because the 'username' header is missing.");
                    return result;
                }
                if (!password.isPresent()) {
                    result.errorMessage = Optional.of("User '" + key + "' not found but cannot be created because the 'password' header is missing.");
                    return result;
                }
                if (!email.isPresent()) {
                    result.errorMessage = Optional.of("User '" + key + "' not found but cannot be created because the 'email' header is missing.");
                    return result;
                }

                UserData data = new UserData.Builder()
                        .username(username.get())
                        .password(password.get())
                        .email(email.get())
                        .build();

                user = jid.isPresent()
                        ? userStore.createUserWithJid(jid.get(), data)
                        : userStore.createUser(data);
                result.createdUsernames.add(username.get());
                existingInfo = new UserInfo.Builder().build();
            }

            UserInfo.Builder info = new UserInfo.Builder().from(existingInfo);
            getCsvValue(headerMap, line, "name").ifPresent(info::name);
            getCsvValue(headerMap, line, "country").ifPresent(info::country);
            getCsvValue(headerMap, line, "institution_name").ifPresent(info::institutionName);
            getCsvValue(headerMap, line, "institution_city").ifPresent(info::institutionCity);
            getCsvValue(headerMap, line, "institution_province").ifPresent(info::institutionProvince);
            infoStore.upsertInfo(user.getJid(), info.build());

            if (headerMap.containsKey("password")) {
                sessionStore.deleteSessionsByUserJid(user.getJid());
            }
        }
        return result;
    }

    private static Optional<String> getCsvValue(Map<String, Integer> headerMap, String[] line, String key) {
        if (!headerMap.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(line[headerMap.get(key)].trim());
    }
}
