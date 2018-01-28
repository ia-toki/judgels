package judgels.jophiel.user.profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_profile")
@Table(indexes = {
        @Index(columnList = "nationality"),
        @Index(columnList = "institution"),
        @Index(columnList = "country"),
        @Index(columnList = "province"),
        @Index(columnList = "city")
        })
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
