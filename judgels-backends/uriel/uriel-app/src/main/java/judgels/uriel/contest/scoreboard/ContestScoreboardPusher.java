package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.ExternalScoreboardData;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestScoreboardPusher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContestScoreboardPusher.class);

    private final Clock clock;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardPusher(Clock clock, ObjectMapper mapper) {
        this.clock = clock;
        this.mapper = mapper;
    }

    public void pushScoreboard(
            String receiverUrl,
            String receiverSecret,
            String contestJid,
            ContestStyle style,
            Map<ContestScoreboardType, Scoreboard> scoreboards) {

        ExternalScoreboardData data = new ExternalScoreboardData.Builder()
                .receiverSecret(receiverSecret)
                .contestJid(contestJid)
                .contestStyle(style)
                .updatedTime(clock.instant())
                .scoreboards(scoreboards)
                .build();

        try {
            byte[] dataBytes = mapper.writeValueAsBytes(data);
            JerseyClientBuilder.createClient()
                    .target(receiverUrl)
                    .request()
                    .post(Entity.entity(dataBytes, MediaType.APPLICATION_JSON));
        } catch (Throwable e) {
            LOGGER.error("Failed to push scoreboard of contest JID " + contestJid, e);
        }
    }
}
