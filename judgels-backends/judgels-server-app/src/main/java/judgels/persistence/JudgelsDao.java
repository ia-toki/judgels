package judgels.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    M findByJid(String jid);
    Optional<M> selectByJid(String jid);
    Map<String, M> selectByJids(Set<String> jids);
    M insertWithJid(String jid, M model);
    M updateByJid(String jid, M model);
    boolean existsByJid(String jid);
}
