package judgels.jophiel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface UserDao extends JudgelsDao<UserModel> {
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
    List<UserModel> selectAllByUsernames(Collection<String> usernames);
}
