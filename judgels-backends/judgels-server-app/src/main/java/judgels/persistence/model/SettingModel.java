package judgels.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "judgels_setting")
public final class SettingModel extends Model {
    @Column(nullable = false, unique = true)
    public String settingKey;

    @Column(columnDefinition = "TEXT")
    public String settingValue;
}
