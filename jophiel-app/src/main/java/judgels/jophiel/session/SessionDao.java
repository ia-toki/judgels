package judgels.jophiel.session;

import java.util.Optional;
import judgels.persistence.Dao;

public interface SessionDao extends Dao<SessionModel> {
    Optional<SessionModel> findByToken(String token);
}
