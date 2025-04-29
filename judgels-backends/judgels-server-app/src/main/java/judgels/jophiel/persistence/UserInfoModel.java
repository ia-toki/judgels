package judgels.jophiel.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
@Entity(name = "jophiel_user_info")
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "gender"),
        @Index(columnList = "country"),
        @Index(columnList = "institutionName"),
        @Index(columnList = "institutionCountry"),
        @Index(columnList = "institutionProvince"),
        @Index(columnList = "institutionCity")})
public class UserInfoModel extends Model {
    @Column(unique = true, nullable = false)
    public String userJid;

    public String name;

    public String gender;

    public String country;

    @Column(columnDefinition = "TEXT")
    public String homeAddress;

    public String shirtSize;

    public String institutionName;

    public String institutionCountry;

    public String institutionProvince;

    public String institutionCity;
}
