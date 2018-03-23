package judgels.jophiel.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.jophiel.persistence.UserModel;
import judgels.persistence.JudgelsDao;

public interface UserDao extends JudgelsDao<UserModel> {
    Optional<UserModel> selectByUsername(String username);
    Optional<UserModel> selectByEmail(String email);
    Map<String, UserModel> selectByJids(Set<String> jids);
    List<UserModel> selectByTerm(String term);
    Map<String, UserModel> selectByUsernames(Set<String> usernames);
}
