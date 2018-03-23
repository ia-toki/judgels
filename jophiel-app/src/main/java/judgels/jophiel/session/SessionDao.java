package judgels.jophiel.session;

import java.util.Optional;
import judgels.jophiel.persistence.SessionModel;
import judgels.persistence.UnmodifiableDao;

public interface SessionDao extends UnmodifiableDao<SessionModel> {
    Optional<SessionModel> findByToken(String token);
}
