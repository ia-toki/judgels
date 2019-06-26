package judgels.jophiel.user.superadmin;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperadminCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperadminCreator.class);

    private final UserStore userStore;
    private final SuperadminRoleStore superadminRoleStore;
    private final SuperadminCreatorConfiguration config;

    public SuperadminCreator(
            UserStore userStore,
            SuperadminRoleStore superadminRoleStore,
            SuperadminCreatorConfiguration config) {

        this.userStore = userStore;
        this.superadminRoleStore = superadminRoleStore;
        this.config = config;
    }

    @UnitOfWork
    public void createIfEnabled() {
        if (config.getEnabled()) {
            String username = config.getUsername().orElse("superadmin");
            String initialPassword = config.getInitialPassword().orElse(username);
            String initialEmail = config.getInitialEmail().orElse(username + "@jophiel.judgels");

            Optional<User> maybeUser = userStore.getUserByUsername(username);
            User user;
            if (maybeUser.isPresent()) {
                user = maybeUser.get();
                LOGGER.info("Superadmin user already exists (username: " + username + ")");
            } else {
                user = userStore.createUser(new UserData.Builder()
                        .username(username)
                        .password(initialPassword)
                        .email(initialEmail)
                        .build());
                LOGGER.info("Created superadmin user (username: " + username + ", email: " + initialEmail + ")");
            }
            superadminRoleStore.setSuperadmin(user.getJid());
        }
    }
}
