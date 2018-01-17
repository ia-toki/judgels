package judgels.persistence;

import java.util.Optional;

public interface JudgelsDao<M extends JudgelsModel> extends Dao<M> {
    Optional<M> selectByJid(String jid);
    M updateByJid(String jid, M model);
}
