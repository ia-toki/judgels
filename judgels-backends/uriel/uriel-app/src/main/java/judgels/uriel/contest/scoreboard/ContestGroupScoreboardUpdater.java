package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestGroup;
import judgels.uriel.api.contest.ContestGroupContest;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardContent;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.group.ContestGroupContestStore;
import judgels.uriel.contest.group.ContestGroupScoreboardStore;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestGroupScoreboardUpdater {
    private final ObjectMapper objectMapper;
    private final ContestStore contestStore;
    private final ContestModuleStore moduleStore;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestGroupContestStore groupContestStore;
    private final ContestGroupScoreboardStore groupScoreboardStore;
    private final ScoreboardProcessorRegistry scoreboardProcessorRegistry;
    private final ContestScoreboardPusher scoreboardPusher;

    public ContestGroupScoreboardUpdater(
            ObjectMapper objectMapper,
            ContestStore contestStore,
            ContestModuleStore moduleStore,
            ContestScoreboardStore scoreboardStore,
            ContestGroupContestStore groupContestStore,
            ContestGroupScoreboardStore groupScoreboardStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            ContestScoreboardPusher scoreboardPusher) {

        this.objectMapper = objectMapper;
        this.contestStore = contestStore;
        this.moduleStore = moduleStore;
        this.scoreboardStore = scoreboardStore;
        this.groupContestStore = groupContestStore;
        this.groupScoreboardStore = groupScoreboardStore;
        this.scoreboardProcessorRegistry = scoreboardProcessorRegistry;
        this.scoreboardPusher = scoreboardPusher;
    }

    @UnitOfWork
    public void update(ContestGroup contestGroup) {
        List<ContestGroupContest> contests = groupContestStore.getContests(contestGroup.getJid());
        Optional<Contest> leader = contestStore.getContestByJid(contests.get(0).getContestJid());
        if (!leader.isPresent()) {
            return;
        }

        StyleModuleConfig styleModuleConfig =
                moduleStore.getStyleModuleConfig(leader.get().getJid(), leader.get().getStyle());

        ScoreboardProcessor processor = scoreboardProcessorRegistry.get(leader.get().getStyle());

        Map<ContestScoreboardType, Scoreboard> combinedScoreboards = new HashMap<>();
        for (ContestScoreboardType type : ContestScoreboardType.values()) {
            List<ScoreboardState> states = new ArrayList<>();
            List<Scoreboard> scoreboards = new ArrayList<>();

            for (ContestGroupContest contest : contests) {
                scoreboardStore.getScoreboard(contest.getContestJid(), type).ifPresent(raw -> {
                    Scoreboard scoreboard = processor.parse(objectMapper, raw.getScoreboard());
                    states.add(scoreboard.getState());
                    scoreboards.add(scoreboard);
                });
            }

            if (scoreboards.isEmpty()) {
                continue;
            }


            ScoreboardContent content = processor.combineContents(styleModuleConfig, scoreboards);
            if (content == null) {
                continue;
            }

            List<String> problemJids = new ArrayList<>();
            List<String> problemAliases = new ArrayList<>();
            List<Integer> problemPoints = new ArrayList<>();

            for (ScoreboardState state : states) {
                problemJids.addAll(state.getProblemJids());
                problemAliases.addAll(state.getProblemAliases());
                state.getProblemPoints().ifPresent(problemPoints::addAll);
            }

            ScoreboardState state = new ScoreboardState.Builder()
                    .problemJids(problemJids)
                    .problemAliases(problemAliases)
                    .problemPoints(problemPoints.isEmpty() ? Optional.empty() : Optional.of(problemPoints))
                    .build();

            Scoreboard scoreboard = processor.create(state, content.getEntries());

            String scoreboardJson;
            try {
                scoreboardJson = objectMapper.writeValueAsString(scoreboard);
            }  catch (IOException e) {
                throw new RuntimeException(e);
            }

            groupScoreboardStore.upsertScoreboard(contestGroup.getJid(), new ContestScoreboardData.Builder()
                    .scoreboard(scoreboardJson)
                    .type(type)
                    .build());

            combinedScoreboards.put(type, scoreboard);
        }

        moduleStore.getExternalScoreboardModuleConfig(leader.get().getJid()).ifPresent(config -> {
            scoreboardPusher.pushScoreboard(
                    config.getReceiverUrl(),
                    config.getReceiverSecret(),
                    contestGroup.getJid(),
                    leader.get().getStyle(),
                    combinedScoreboards);
        });
    }
}
