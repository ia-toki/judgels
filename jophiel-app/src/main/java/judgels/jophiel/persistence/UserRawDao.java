package judgels.jophiel.persistence;

import java.util.List;

public interface UserRawDao {
    List<UserModel> selectByTerm(String term);
}
