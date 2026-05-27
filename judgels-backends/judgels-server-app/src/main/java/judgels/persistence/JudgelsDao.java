package judgels.persistence;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    M findByJid(String jid);
    Optional<M> selectByJid(String jid);
    Map<String, M> selectByJids(Collection<String> jids);
    M insertWithJid(String jid, M model);
    M updateByJid(String jid, M model);
    boolean existsByJid(String jid);
}
