package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
public abstract class UnmodifiableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public String createdBy;

    public String createdIp;

    @Column(nullable = false)
    public Instant createdAt;
}
