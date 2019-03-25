package judgels.persistence.api.dump;

import java.time.Instant;

public interface Dump extends UnmodifiableDump {
    String getUpdatedBy();
    String getUpdatedIp();
    Instant getUpdatedAt();
}
