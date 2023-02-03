package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
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

        Scoreboard scoreboard = processor.parse(mapper, raw.getScoreboard());
        scoreboard = filterContestantJidsIfNecessary(scoreboard, processor, contest, userJid, canSupervise);
        scoreboard = filterProblemJidsIfNecessary(scoreboard, processor, contest, showAllProblems, canSupervise);

        return scoreboard;
    }

    public Scoreboard paginateScoreboard(Scoreboard scoreboard, Contest contest, int page, int pageSize) {
        ScoreboardProcessor processor = processorRegistry.get(contest.getStyle());

        List<? extends List<? extends ScoreboardEntry>> partition =
                Lists.partition(scoreboard.getContent().getEntries(), pageSize);

        List<? extends ScoreboardEntry> partitionPage;
        if (page <= partition.size()) {
            partitionPage = partition.get(page - 1);
        } else {
            partitionPage = ImmutableList.of();
        }

        return processor.create(scoreboard.getState(), partitionPage);
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
        if (!scoreboardModuleConfig.getIsIncognitoScoreboard()) {
            return scoreboard;
        }

        List<? extends ScoreboardEntry> filteredEntries = scoreboard.getContent().getEntries()
                .stream()
                .filter(e -> e.getContestantJid().equals(userJid))
                .map(processor::clearEntryRank)
                .collect(Collectors.toList());

        return processor.create(scoreboard.getState(), filteredEntries);
    }

    private Scoreboard filterProblemJidsIfNecessary(
            Scoreboard scoreboard,
            ScoreboardProcessor processor,
            Contest contest,
            boolean showAllProblems,
            boolean canSupervise) {

        if (showAllProblems || contest.getStyle() != ContestStyle.IOI) {
            return scoreboard;
        }

        boolean isIncognito = moduleStore.getScoreboardModuleConfig(contest.getJid()).getIsIncognitoScoreboard();
        Optional<String> previousContestJid = moduleStore.getMergedScoreboardModuleConfig(contest.getJid())
                .flatMap(c -> c.getPreviousContestJid());

        Set<String> openProblemJids = new HashSet<>();
        openProblemJids.addAll(problemStore.getOpenProblemJids(contest.getJid()));

        if (previousContestJid.isPresent() && (canSupervise || !isIncognito)) {
            openProblemJids.addAll(problemStore.getOpenProblemJids(previousContestJid.get()));
        }

        if (openProblemJids.size() != scoreboard.getState().getProblemJids().size()) {
            IoiStyleModuleConfig config = moduleStore.getIoiStyleModuleConfig(contest.getJid());
            IoiScoreboardProcessor ioiProcessor = (IoiScoreboardProcessor) processor;
            IoiScoreboard ioiScoreboard = (IoiScoreboard) scoreboard;
            return ioiProcessor.filterProblemJids(ioiScoreboard, ImmutableSet.copyOf(openProblemJids), config);
        }
        return scoreboard;
    }
}
