package judgels.jophiel.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface UserProfileDao extends Dao<UserProfileModel> {
    Optional<UserProfileModel> selectByUserJid(String userJid);
    Map<String, UserProfileModel> selectAllByUserJids(Set<String> userJids);
}
