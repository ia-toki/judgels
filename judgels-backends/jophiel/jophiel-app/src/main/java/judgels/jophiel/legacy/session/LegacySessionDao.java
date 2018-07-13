package judgels.jophiel.legacy.session;

import java.util.Optional;
import judgels.persistence.UnmodifiableDao;

public interface LegacySessionDao extends UnmodifiableDao<LegacySessionModel> {
    Optional<LegacySessionModel> findByAuthCode(String authCode);
    Optional<LegacySessionModel> findByToken(String token);
}
