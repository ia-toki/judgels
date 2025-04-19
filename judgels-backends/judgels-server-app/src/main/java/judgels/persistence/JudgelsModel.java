package judgels.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
public abstract class JudgelsModel extends Model {
    @Column(unique = true, nullable = false)
    public String jid;
}
