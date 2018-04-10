package judgels.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
public abstract class Model extends UnmodifiableModel {
    public String updatedBy;

    public String updatedIp;

    @Column(nullable = false)
    public Instant updatedAt;
}
