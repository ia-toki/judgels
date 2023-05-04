package judgels.jophiel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;

public interface UserInfoDao extends Dao<UserInfoModel> {
    Optional<UserInfoModel> selectByUserJid(String userJid);
    List<UserInfoModel> selectAllByUserJids(Set<String> userJids);
}
