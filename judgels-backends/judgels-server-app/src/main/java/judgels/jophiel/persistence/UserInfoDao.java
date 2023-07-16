package judgels.jophiel.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;

public interface UserInfoDao extends Dao<UserInfoModel> {
    Optional<UserInfoModel> selectByUserJid(String userJid);
    List<UserInfoModel> selectAllByUserJids(Collection<String> userJids);
}
