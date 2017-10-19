package judgels.jophiel.user;

import java.util.Optional;
import judgels.jophiel.api.user.User;

public interface UserStore {
    Optional<User> findByJid(String userJid);
    void insert(User user);
}
