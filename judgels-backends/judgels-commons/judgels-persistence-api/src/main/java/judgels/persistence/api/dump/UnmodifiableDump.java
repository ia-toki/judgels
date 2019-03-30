package judgels.persistence.api.dump;

import java.time.Instant;
import java.util.Optional;

public interface UnmodifiableDump {
    DumpImportBehavior getImportBehavior();
    Optional<String> getCreatedBy();
    Optional<String> getCreatedIp();
    Optional<Instant> getCreatedAt();
}
