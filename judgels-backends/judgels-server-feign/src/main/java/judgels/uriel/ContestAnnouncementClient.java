package judgels.uriel;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;

public interface ContestAnnouncementClient {
    @RequestLine("GET /api/v2/contests/{contestJid}/announcements")
    @Headers("Authorization: Bearer {token}")
    ContestAnnouncementsResponse getAnnouncements(@Param("token") String token, @Param("contestJid") String contestJid);

    @RequestLine("POST /api/v2/contests/{contestJid}/announcements")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestAnnouncement createAnnouncement(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            ContestAnnouncementData data);

    @RequestLine("PUT /api/v2/contests/{contestJid}/announcements/{announcementJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    ContestAnnouncement updateAnnouncement(
            @Param("token") String token,
            @Param("contestJid") String contestJid,
            @Param("announcementJid") String announcementJid,
            ContestAnnouncementData data);
}
