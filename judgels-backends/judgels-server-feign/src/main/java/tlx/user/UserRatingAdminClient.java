package tlx.user;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import tlx.api.user.rating.UserRatingUpdateData;

public interface UserRatingAdminClient {
    @RequestLine("POST /api/v2/admin/user-rating")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    void updateRatings(@Param("token") String token, UserRatingUpdateData data);
}
