package judgels.jophiel.user;

import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface UserDao extends JudgelsDao<UserModel> {
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
}
