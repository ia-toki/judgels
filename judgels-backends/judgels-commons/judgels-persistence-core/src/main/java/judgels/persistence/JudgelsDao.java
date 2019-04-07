package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.api.dump.JudgelsDump;

public interface JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    Optional<M> selectByJid(String jid);
    Map<String, M> selectByJids(Set<String> jids);
    M updateByJid(String jid, M model);
    boolean existsByJid(String jid);

    void setModelMetadataFromDump(M model, JudgelsDump dump);

    @Deprecated void persist(M model, int childIndex, String actor, String ipAddress);
    @Deprecated M findByJid(String jid);
    @Deprecated List<M> getByJids(Collection<String> jids);
}
