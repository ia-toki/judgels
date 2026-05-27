package judgels.persistence.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.model.UserInfoModel;

public interface UserInfoDao extends Dao<UserInfoModel> {
    Optional<UserInfoModel> selectByUserJid(String userJid);
    List<UserInfoModel> selectAllByUserJids(Collection<String> userJids);
}
