package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ioi.IoiScoreboardProcessor;

public class ContestScoreboardBuilder {
    private final ScoreboardProcessorRegistry processorRegistry;
    private final ContestModuleStore moduleStore;
    private final ContestProblemStore problemStore;
    private final ObjectMapper mapper;

    @Inject
    public ContestScoreboardBuilder(
            ScoreboardProcessorRegistry processorRegistry,
            ContestModuleStore moduleStore,
            ContestProblemStore problemStore,
            ObjectMapper mapper) {

        this.processorRegistry = processorRegistry;
        this.moduleStore = moduleStore;
        this.problemStore = problemStore;
        this.mapper = mapper;
    }

    public Scoreboard buildScoreboard(
            RawContestScoreboard raw,
            Contest contest,
            String userJid,
            boolean canSupervise,
            boolean showAllProblems) {

        ScoreboardProcessor processor = processorRegistry.get(contest.getStyle());

        Scoreboard scoreboard = processor.parseFromString(mapper, raw.getScoreboard());
        scoreboard = filterContestantJidsIfNecessary(scoreboard, processor, contest, userJid, canSupervise);
        scoreboard = filterProblemJidsIfNecessary(scoreboard, processor, contest, showAllProblems);

        return scoreboard;
    }

    private Scoreboard filterContestantJidsIfNecessary(
            Scoreboard scoreboard,
            ScoreboardProcessor processor,
            Contest contest,
            String userJid,
            boolean canSupervise) {

        if (canSupervise) {
            return scoreboard;
        }

        ScoreboardModuleConfig scoreboardModuleConfig = moduleStore.getScoreboardModuleConfig(contest.getJid());
        if (scoreboardModuleConfig.getIsIncognitoScoreboard()) {
            return processor.filterContestantJids(scoreboard, ImmutableSet.of(userJid));
        }

        return scoreboard;
    }

    private Scoreboard filterProblemJidsIfNecessary(
            Scoreboard scoreboard,
            ScoreboardProcessor processor,
            Contest contest,
            boolean showAllProblems) {

        if (showAllProblems || contest.getStyle() != ContestStyle.IOI) {
            return scoreboard;
        }

        Set<String> openProblemJids = ImmutableSet.copyOf(problemStore.getOpenProblemJids(contest.getJid()));
        if (openProblemJids.size() != scoreboard.getState().getProblemJids().size()) {
            IoiStyleModuleConfig config = moduleStore.getIoiStyleModuleConfig(contest.getJid());
            IoiScoreboardProcessor ioiProcessor = (IoiScoreboardProcessor) processor;
            return ioiProcessor.filterProblemJids((IoiScoreboard) scoreboard, openProblemJids, config);
        }
        return scoreboard;
    }
}
