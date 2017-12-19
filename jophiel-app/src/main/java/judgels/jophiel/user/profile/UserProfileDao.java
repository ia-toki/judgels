package judgels.jophiel.user.profile;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserProfileDao extends Dao<UserProfileModel> {
    Optional<UserProfileModel> selectByUserJid(String userJid);
}
