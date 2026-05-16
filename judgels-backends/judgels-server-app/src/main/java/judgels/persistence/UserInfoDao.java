package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserInfoDao extends Dao<UserInfoModel> {
    Optional<UserInfoModel> selectByUserJid(String userJid);
    List<UserInfoModel> selectAllByUserJids(Collection<String> userJids);
}
