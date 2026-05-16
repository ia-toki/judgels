package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDao extends JudgelsDao<UserModel> {
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
    List<UserModel> selectAllByUsernames(Collection<String> usernames);
}
