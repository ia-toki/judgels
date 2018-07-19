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

    public static final String USERNAME = "superadmin";
    public static final String PASSWORD = "superadmin";

    private final UserStore userStore;
    private final SuperadminRoleStore superadminRoleStore;

    public SuperadminCreator(UserStore userStore, SuperadminRoleStore superadminRoleStore) {
        this.userStore = userStore;
        this.superadminRoleStore = superadminRoleStore;
    }

    @UnitOfWork
    public void create() {
        Optional<User> maybeUser = userStore.getUserByUsername(USERNAME);
        User user;
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            LOGGER.info("superadmin user already exists");
        } else {
            user = userStore.createUser(new UserData.Builder()
                    .username(USERNAME)
                    .password(PASSWORD)
                    .email(USERNAME + "@jophiel.judgels")
                    .build());
            LOGGER.info("Created superadmin user");
        }
        superadminRoleStore.setSuperadmin(user.getJid());
    }
}
