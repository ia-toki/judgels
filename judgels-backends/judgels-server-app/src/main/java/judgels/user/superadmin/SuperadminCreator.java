package judgels.user.superadmin;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import judgels.api.user.User;
import judgels.api.user.UserData;
import judgels.api.user.role.UserRole;
import judgels.user.UserStore;
import judgels.user.role.UserRoleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperadminCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperadminCreator.class);
    private static final String SUPERADMIN_USERNAME = "superadmin";
    private static final String SUPERADMIN_INITIAL_EMAIL = SUPERADMIN_USERNAME + "@judgels.com";

    private final UserStore userStore;
    private final UserRoleStore userRoleStore;
    private final Optional<SuperadminConfiguration> config;

    public SuperadminCreator(
            UserStore userStore,
            UserRoleStore userRoleStore,
            Optional<SuperadminConfiguration> config) {

        this.userStore = userStore;
        this.userRoleStore = userRoleStore;
        this.config = config;
    }

    @UnitOfWork
    public void ensureSuperadminExists() {
        String initialPassword = config.orElse(SuperadminConfiguration.DEFAULT).getInitialPassword();

        Optional<User> maybeUser = userStore.getUserByUsername(SUPERADMIN_USERNAME);
        User user;
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            LOGGER.info("Superadmin user already exists");
        } else {
            user = userStore.createUser(new UserData.Builder()
                    .username(SUPERADMIN_USERNAME)
                    .password(initialPassword)
                    .email(SUPERADMIN_INITIAL_EMAIL)
                    .build());
            LOGGER.info("Created superadmin user");
        }
        userRoleStore.addRoles(user.getJid(), Set.of(UserRole.SUPERADMIN));
    }
}
