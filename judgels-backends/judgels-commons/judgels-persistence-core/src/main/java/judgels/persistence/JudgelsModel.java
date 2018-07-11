package judgels.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("checkstyle:visibilitymodifier")
@MappedSuperclass
public abstract class JudgelsModel extends Model {
    @Column(unique = true, nullable = false)
    public String jid;
}
