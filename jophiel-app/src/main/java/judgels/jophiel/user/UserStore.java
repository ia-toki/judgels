package judgels.jophiel.user;

import java.util.Optional;
import judgels.jophiel.api.user.User;

public interface UserStore {
    Optional<User> findById(long userId);
    void insert(User user);
}
