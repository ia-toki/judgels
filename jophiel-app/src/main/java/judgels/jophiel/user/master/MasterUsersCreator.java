package judgels.jophiel.user.master;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterUsersCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterUsersCreator.class);

    public static final String DEFAULT_PASSWORD = "master";

    private final UserStore userStore;
    private final Set<String> masterUsers;

    public MasterUsersCreator(UserStore userStore, Set<String> masterUsers) {
        this.userStore = userStore;
        this.masterUsers = masterUsers;
    }

    @UnitOfWork
    public void create() {
        LOGGER.info("Creating master users...");
        for (String username : masterUsers) {
            if (userStore.findUserByUsername(username).isPresent()) {
                LOGGER.info("Master user {} already exists.");
            } else {
                userStore.createUser(new UserData.Builder()
                        .username(username)
                        .password(DEFAULT_PASSWORD)
                        .name(username)
                        .email(username + "@jophiel.judgels")
                        .build());
                LOGGER.info("Created master user {}.", username);
            }
        }
        LOGGER.info("Done creating master users.");
    }
}
