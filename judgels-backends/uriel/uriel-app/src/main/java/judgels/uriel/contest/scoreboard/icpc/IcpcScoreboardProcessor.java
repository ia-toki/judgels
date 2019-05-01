package judgels.uriel.contest.scoreboard.icpc;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
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
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardContent;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.IcpcScoreboard.IcpcScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class IcpcScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public IcpcScoreboard parse(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, IcpcScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IcpcScoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries) {
        return new IcpcScoreboard.Builder()
                .state(state)
                .content(new IcpcScoreboardContent.Builder()
                        .entries(Lists.transform(entries, e -> (IcpcScoreboardEntry) e))
                        .build())
                .build();
    }

    @Override
    public List<IcpcScoreboardEntry> computeEntries(
            ScoreboardState scoreboardState,
            Contest contest,
            StyleModuleConfig styleModuleConfig,
            Set<ContestContestant> contestants,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions,
            Optional<Instant> freezeTime) {

        IcpcStyleModuleConfig icpcStyleModuleConfig = (IcpcStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();
        Set<String> problemJidsSet = ImmutableSet.copyOf(problemJids);
        Set<String> contestantJids = contestants.stream().map(ContestContestant::getUserJid).collect(toSet());
        Map<String, Optional<Instant>> contestantStartTimesMap = contestants.stream()
                .collect(toMap(ContestContestant::getUserJid, ContestContestant::getContestStartTime));

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

                if (verdict.equals(Verdict.ACCEPTED)) {
                    if (firstSolveSubmissionJid.get(problemJid).equals(submission.getJid())) {
                        problemStateMap.put(problemJid, IcpcScoreboardProblemState.FIRST_ACCEPTED);
                    } else {
                        problemStateMap.put(problemJid, IcpcScoreboardProblemState.ACCEPTED);
                    }

                    penaltyMap.put(problemJid, convertPenaltyToMinutes(penalty));
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

        return sortEntriesAndAssignRanks(entries);
    }


    @Override
    public IcpcScoreboardEntry clearEntryRank(ScoreboardEntry entry) {
        return new IcpcScoreboardEntry.Builder()
                .from((IcpcScoreboardEntry) entry)
                .rank(-1)
                .build();
    }

    private List<IcpcScoreboardEntry> sortEntriesAndAssignRanks(List<IcpcScoreboardEntry> entries) {
        ScoreboardEntryComparator<IcpcScoreboardEntry> comparator = new StandardIcpcScoreboardEntryComparator();
        entries.sort(comparator);

        List<IcpcScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < entries.size(); i++) {
            if (i != 0 && comparator.compareWithoutTieBreakerForEqualRanks(entries.get(i), entries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new IcpcScoreboardEntry.Builder().from(entries.get(i)).rank(currentRank).build());
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
