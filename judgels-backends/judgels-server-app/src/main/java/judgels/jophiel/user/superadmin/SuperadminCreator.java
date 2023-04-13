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
    private static final String SUPERADMIN_USERNAME = "superadmin";
    private static final String SUPERADMIN_INITIAL_EMAIL = SUPERADMIN_USERNAME + "@jophiel.judgels";

    private final UserStore userStore;
    private final SuperadminRoleStore superadminRoleStore;
    private final Optional<SuperadminCreatorConfiguration> config;

    public SuperadminCreator(
            UserStore userStore,
            SuperadminRoleStore superadminRoleStore,
            Optional<SuperadminCreatorConfiguration> config) {

        this.userStore = userStore;
        this.superadminRoleStore = superadminRoleStore;
        this.config = config;
    }

    @UnitOfWork
    public void ensureSuperadminExists() {
        String initialPassword = config.orElse(SuperadminCreatorConfiguration.DEFAULT).getInitialPassword();

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
        superadminRoleStore.setSuperadmin(user.getJid());
    }
}
