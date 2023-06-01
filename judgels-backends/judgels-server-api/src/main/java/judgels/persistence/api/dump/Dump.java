package judgels.persistence.api.dump;

import java.time.Instant;
import java.util.Optional;

public interface Dump extends UnmodifiableDump {
    Optional<String> getUpdatedBy();
    Optional<String> getUpdatedIp();
    Optional<Instant> getUpdatedAt();
}
