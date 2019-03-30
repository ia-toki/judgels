package judgels.persistence.api.dump;

import java.util.Optional;

public interface JudgelsDump extends Dump {
    Optional<String> getJid();
}
