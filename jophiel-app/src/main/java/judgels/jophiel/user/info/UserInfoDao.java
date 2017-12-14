package judgels.jophiel.user.info;

import java.util.Optional;
import judgels.persistence.Dao;

public interface UserInfoDao extends Dao<UserInfoModel> {
    Optional<UserInfoModel> selectByUserJid(String userJid);
}
