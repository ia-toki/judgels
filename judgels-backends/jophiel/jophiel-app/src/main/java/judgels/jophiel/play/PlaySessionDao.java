package judgels.jophiel.play;

import java.util.Optional;
import judgels.persistence.UnmodifiableDao;

public interface PlaySessionDao extends UnmodifiableDao<PlaySessionModel> {
    Optional<PlaySessionModel> getByAuthCode(String authCode);
}
