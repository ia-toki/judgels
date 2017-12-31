package judgels.jophiel.user.superadmin;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperadminCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperadminCreator.class);

    public static final String USERNAME = "superadmin";
    public static final String PASSWORD = "superadmin";

    private final UserStore userStore;

    public SuperadminCreator(UserStore userStore) {
        this.userStore = userStore;
    }

    @UnitOfWork
    public void create() {
        if (userStore.findUserByUsername(USERNAME).isPresent()) {
            LOGGER.info("superadmin user already exists.");
        } else {
            userStore.createUser(new UserData.Builder()
                    .username(USERNAME)
                    .password(PASSWORD)
                    .email(USERNAME + "@jophiel.judgels")
                    .build());
            LOGGER.info("Created superadmin user.");
        }
    }
}
