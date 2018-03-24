package judgels.jophiel.persistence;

import java.util.Optional;
import judgels.persistence.UnmodifiableDao;

public interface SessionDao extends UnmodifiableDao<SessionModel> {
    Optional<SessionModel> selectByToken(String token);
}
