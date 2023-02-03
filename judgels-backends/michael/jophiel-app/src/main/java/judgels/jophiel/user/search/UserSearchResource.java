package judgels.jophiel.user.search;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.jophiel.user.UserStore;

public class UserSearchResource implements UserSearchService {
    private final UserStore userStore;

    @Inject
    public UserSearchResource(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean usernameExists(String username) {
        return userStore.getUserByUsername(username).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean emailExists(String email) {
        return userStore.getUserByEmail(email).isPresent();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, String> translateUsernamesToJids(Set<String> usernames) {
        return userStore.translateUsernamesToJids(usernames);
    }
}
