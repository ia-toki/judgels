package judgels.jophiel.user.profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_profile")
public class UserProfileModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    public String name;

    public String gender;

    public String nationality;

    @Column(columnDefinition = "TEXT")
    public String homeAddress;

    public String shirtSize;

    public String institution;

    public String country;

    public String province;

    public String city;
}
