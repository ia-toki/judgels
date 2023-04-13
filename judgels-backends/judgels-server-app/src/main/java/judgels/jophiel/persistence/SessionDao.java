package judgels.jophiel.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.UnmodifiableDao;

public interface SessionDao extends UnmodifiableDao<SessionModel> {
    Optional<SessionModel> selectByToken(String token);
    List<SessionModel> selectAllByUserJid(String userJid);
    List<SessionModel> selectAllByUserJids(Set<String> userJids);
    List<SessionModel> selectAllOlderThan(Instant time);
}
