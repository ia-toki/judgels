package judgels.jophiel;

import feign.Param;
import feign.RequestLine;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;

public interface ProfileClient {
    @RequestLine("GET /api/v2/profiles/top")
    Page<Profile> getTopRatedProfiles();

    @RequestLine("GET /api/v2/profiles/{userJid}/basic")
    BasicProfile getBasicProfile(@Param("userJid") String userJid);
}
