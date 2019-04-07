package judgels.uriel.contest.scoreboard.icpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.gabriel.api.Verdict;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IcpcScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public Scoreboard parseFromString(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IcpcScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IcpcScoreboardEntry> getEntries(Scoreboard scoreboard) {
        return ((IcpcScoreboard) scoreboard).getContent().getEntries();
    }

    @Override
    public Scoreboard replaceEntries(Scoreboard scoreboard, List<?> entries) {
        IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard;
        return new IcpcScoreboard.Builder()
                .state(icpcScoreboard.getState())
                .content(new IcpcScoreboardContent.Builder()
                            .entries((List<? extends IcpcScoreboardEntry>) entries)
                            .build())
                .build();
    }

    @Override
    public int getTotalEntries(Scoreboard scoreboard) {
        return ((IcpcScoreboard) scoreboard).getContent().getEntries().size();
    }

    @Override
    public String computeToString(
            ObjectMapper mapper,
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Map<String, Optional<Instant>> contestantStartTimesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime) {
        IcpcStyleModuleConfig icpcStyleModuleConfig = (IcpcStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = scoreboardState.getContestantJids();

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        Map<String, List<Submission>> frozenSubmissionsMap = new HashMap<>();
        contestantJids.forEach(c -> {
            submissionsMap.put(c, new ArrayList<>());
            frozenSubmissionsMap.put(c, new ArrayList<>());
        });

        List<Submission> filteredSubmissions = programmingSubmissions.stream()
                .filter(s -> contestantJids.contains(s.getUserJid()))
                .filter(s -> problemJidsSet.contains(s.getProblemJid()))
                .collect(Collectors.toList());

        filteredSubmissions.stream()
                .filter(s -> s.getLatestGrading().isPresent())
                .filter(s -> s.getTime().isBefore(freezeTime.orElse(Instant.MAX)))
                .forEach(s -> submissionsMap.get(s.getUserJid()).add(s));

        filteredSubmissions.stream()
                .filter(s -> !s.getTime().isBefore(freezeTime.orElse(Instant.MAX)))
                .forEach(s -> frozenSubmissionsMap.get(s.getUserJid()).add(s));

        Map<String, String> firstSolveSubmissionJid = new HashMap<>();
        filteredSubmissions.stream()
                .filter(s -> s.getLatestGrading().isPresent()
                        && s.getLatestGrading().get().getVerdict().equals(Verdict.ACCEPTED)
                        && contestantJids.contains(s.getUserJid()))
                .forEach(s -> firstSolveSubmissionJid.putIfAbsent(s.getProblemJid(), s.getJid()));

        List<IcpcScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            long lastAcceptedPenalty = 0;

            Map<String, Integer> attemptsMap = new HashMap<>();
            Map<String, Long> penaltyMap = new HashMap<>();
            Map<String, IcpcScoreboardProblemState> problemStateMap = new HashMap<>();

            problemJids.forEach(p -> {
                attemptsMap.put(p, 0);
                penaltyMap.put(p, 0L);
                problemStateMap.put(p, IcpcScoreboardProblemState.NOT_ACCEPTED);
            });

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();

                if (isAccepted(problemStateMap.get(problemJid))) {
                    continue;
                }

                Verdict verdict = submission.getLatestGrading().get().getVerdict();
                if (verdict.equals(Verdict.PENDING)) {
                    continue;
                }

                attemptsMap.put(problemJid, attemptsMap.get(problemJid) + 1);

                long penalty = computePenalty(
                        submission.getTime(),
                        contestantStartTimesMap.get(contestantJid),
                        contest.getBeginTime());
                penaltyMap.put(problemJid, convertPenaltyToMinutes(penalty));

                if (verdict.equals(Verdict.ACCEPTED)) {
                    if (firstSolveSubmissionJid.get(problemJid).equals(submission.getJid())) {
                        problemStateMap.put(problemJid, IcpcScoreboardProblemState.FIRST_ACCEPTED);
                    } else {
                        problemStateMap.put(problemJid, IcpcScoreboardProblemState.ACCEPTED);
                    }

                    lastAcceptedPenalty = penalty;
                }
            }

            for (Submission submission : frozenSubmissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();
                if (isAccepted(problemStateMap.get(problemJid))) {
                    continue;
                }

                problemStateMap.put(problemJid, IcpcScoreboardProblemState.FROZEN);
            }

            entries.add(new IcpcScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .totalAccepted((int) problemStateMap.values()
                            .stream()
                            .filter(this::isAccepted)
                            .count())
                    .totalPenalties(penaltyMap.keySet().stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToLong(p -> ((attemptsMap.get(p) - 1)
                                    * icpcStyleModuleConfig.getWrongSubmissionPenalty())
                                    + penaltyMap.get(p))
                            .sum())
                    .lastAcceptedPenalty(lastAcceptedPenalty)
                    .attemptsList(problemJids
                            .stream()
                            .map(attemptsMap::get)
                            .collect(Collectors.toList()))
                    .penaltyList(problemJids
                            .stream()
                            .map(penaltyMap::get)
                            .collect(Collectors.toList()))
                    .problemStateList(problemJids
                            .stream()
                            .map(problemStateMap::get)
                            .collect(Collectors.toList()))
                    .build());
        }

        entries = sortEntriesAndAssignRanks(entries);

        try {
            return mapper.writeValueAsString(new IcpcScoreboard.Builder()
                    .state(scoreboardState)
                    .content(new IcpcScoreboardContent.Builder()
                            .entries(entries)
                            .build())
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scoreboard filterContestantJids(Scoreboard scoreboard, Set<String> contestantJids) {
        IcpcScoreboard icpcScoreboard = (IcpcScoreboard) scoreboard;

        Set<String> filteredContestantJids = icpcScoreboard.getState().getContestantJids()
                .stream()
                .filter(contestantJids::contains)
                .collect(Collectors.toSet());

        ScoreboardState filteredState = new ScoreboardState.Builder()
                .from(icpcScoreboard.getState())
                .contestantJids(filteredContestantJids)
                .build();

        List<IcpcScoreboardEntry> filteredEntries = icpcScoreboard.getContent().getEntries()
                .stream()
                .filter(entry -> contestantJids.contains(entry.getContestantJid()))
                .map(entry -> new IcpcScoreboardEntry.Builder()
                        .from(entry)
                        .rank(-1)
                        .build())
                .collect(Collectors.toList());

        return new IcpcScoreboard.Builder()
                .state(filteredState)
                .content(new IcpcScoreboardContent.Builder().entries(filteredEntries).build())
                .build();
    }

    private List<IcpcScoreboardEntry> sortEntriesAndAssignRanks(List<IcpcScoreboardEntry> scoreboardEntries) {
        Comparator<IcpcScoreboardEntry> comparator = new StandardIcpcScoreboardEntryComparator();
        scoreboardEntries.sort(comparator);

        List<IcpcScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < scoreboardEntries.size(); i++) {
            if (i != 0 && comparator.compare(scoreboardEntries.get(i), scoreboardEntries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new IcpcScoreboardEntry.Builder().from(scoreboardEntries.get(i)).rank(currentRank).build());
        }

        return result;
    }

    private long computePenalty(
            Instant submissionTime,
            Optional<Instant> contestantStartTime,
            Instant contestBeginTime) {

        return submissionTime.toEpochMilli() - contestantStartTime.orElse(contestBeginTime).toEpochMilli();
    }

    private long convertPenaltyToMinutes(long penaltyInMilliseconds) {
        long penaltyInMinutes = TimeUnit.MILLISECONDS.toMinutes(penaltyInMilliseconds);
        if (TimeUnit.MINUTES.toMillis(penaltyInMinutes) != penaltyInMilliseconds) {
            penaltyInMinutes++;
        }

        return penaltyInMinutes;
    }

    private boolean isAccepted(IcpcScoreboardProblemState problemState) {
        return problemState.equals(IcpcScoreboardProblemState.FIRST_ACCEPTED)
                || problemState.equals(IcpcScoreboardProblemState.ACCEPTED);
    }
}
