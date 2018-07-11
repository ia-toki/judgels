package judgels.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    Optional<M> selectByJid(String jid);
    Map<String, M> selectByJids(Set<String> jids);
    M updateByJid(String jid, M model);
}
