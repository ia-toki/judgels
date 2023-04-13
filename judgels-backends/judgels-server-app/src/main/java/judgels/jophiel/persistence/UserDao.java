package judgels.jophiel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;

public interface UserDao extends JudgelsDao<UserModel> {
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
    List<UserModel> selectAllByTerm(String term);
    List<UserModel> selectAllByUsernames(Set<String> usernames);
}
