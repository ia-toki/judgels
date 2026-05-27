package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
public abstract class Model extends UnmodifiableModel {
    public String updatedBy;

    public String updatedIp;

    @Column(nullable = false)
    public Instant updatedAt;
}
