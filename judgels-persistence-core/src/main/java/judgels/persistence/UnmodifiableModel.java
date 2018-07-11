package judgels.persistence;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
