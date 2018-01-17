package judgels.jophiel.user.superadmin;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.role.RoleStore;
import judgels.jophiel.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperadminCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperadminCreator.class);

    public static final String USERNAME = "superadmin";
    public static final String PASSWORD = "superadmin";

    private final UserStore userStore;
    private final RoleStore roleStore;

    public SuperadminCreator(UserStore userStore, RoleStore roleStore) {
        this.userStore = userStore;
        this.roleStore = roleStore;
    }

    @UnitOfWork
    public void create() {
        if (userStore.findUserByUsername(USERNAME).isPresent()) {
            LOGGER.info("superadmin user already exists");
        } else {
            User user = userStore.createUser(new UserData.Builder()
                    .username(USERNAME)
                    .password(PASSWORD)
                    .email(USERNAME + "@jophiel.judgels")
                    .build());
            roleStore.setSuperadmin(user.getJid());
            LOGGER.info("Created superadmin user");
        }
    }
}
