package judgels.persistence.api.dump;

import java.time.Instant;

public interface UnmodifiableDump {
    String getCreatedBy();
    String getCreatedIp();
    Instant getCreatedAt();
}
