package tlx.jophiel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import tlx.jophiel.api.user.rating.UserRatingUpdateData;

public interface UserRatingClient {
    @RequestLine("POST /api/v2/user-rating")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateRatings(@Param("token") String token, UserRatingUpdateData data);

    @RequestLine("GET /api/v2/user-rating/history?userJid={userJid}")
    List<UserRatingEvent> getRatingHistory(@Param("userJid") String userJid);
}
