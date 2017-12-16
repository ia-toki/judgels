package judgels.jophiel.user.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_info")
public class UserInfoModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    public String name;

    public String gender;

    @Column(columnDefinition = "TEXT")
    public String streetAddress;

    public String postalCode;

    @Column(columnDefinition = "TEXT")
    public String institution;

    public String city;

    public String provinceOrState;

    public String country;

    public String shirtSize;
}
