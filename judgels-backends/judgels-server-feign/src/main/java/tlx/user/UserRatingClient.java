package tlx.user;

import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.api.user.rating.UserRatingEvent;

public interface UserRatingClient {
    @RequestLine("GET /api/v2/user-rating/history?userJid={userJid}")
    List<UserRatingEvent> getRatingHistory(@Param("userJid") String userJid);
}
