package judgels.uriel.contest.scoreboard.gcj;

import static java.util.Collections.emptyMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.gabriel.api.ScoringConfig;
import judgels.gabriel.api.Verdict;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.GcjStyleModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardContent;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.GcjScoreboard.GcjScoreboardProblemState;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.api.contest.scoreboard.ScoreboardState;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;
import judgels.uriel.contest.scoreboard.ScoreboardIncrementalContent;
import judgels.uriel.contest.scoreboard.ScoreboardProcessResult;
import judgels.uriel.contest.scoreboard.ScoreboardProcessor;

public class GcjScoreboardProcessor implements ScoreboardProcessor {
    @Override
    public GcjScoreboard parse(ObjectMapper mapper, String json) {
        try {
            return mapper.readValue(json, GcjScoreboard.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GcjScoreboard create(ScoreboardState state, List<? extends ScoreboardEntry> entries) {
        return new GcjScoreboard.Builder()
                .state(state)
                .content(new GcjScoreboardContent.Builder()
                        .entries(Lists.transform(entries, e -> (GcjScoreboardEntry) e))
                        .build())
                .build();
    }

    @Override
    public boolean requiresGradingDetails(StyleModuleConfig styleModuleConfig) {
        return false;
    }

    @Override
    public ScoreboardProcessResult process(
            ScoreboardState scoreboardState,
            Optional<ScoreboardIncrementalContent> incrementalContent,
            StyleModuleConfig styleModuleConfig,
            Map<String, Set<ContestContestant>> contestContestantsMap,
            Map<String, Instant> contestBeginTimesMap,
            Map<String, Instant> contestFreezeTimesMap,
            Map<String, ScoringConfig> problemScoringConfigsMap,
            Map<String, Profile> profilesMap,
            List<Submission> programmingSubmissions,
            List<ItemSubmission> bundleItemSubmissions) {

        GcjStyleModuleConfig gcjStyleModuleConfig = (GcjStyleModuleConfig) styleModuleConfig;

        List<String> problemJids = scoreboardState.getProblemJids();

        Set<String> contestantJids = new HashSet<>();
        Map<String, Map<String, Optional<Instant>>> contestantStartTimesMap = new HashMap<>();
        for (var entry : contestContestantsMap.entrySet()) {
            contestantStartTimesMap.put(entry.getKey(), new HashMap<>());
            for (ContestContestant contestant : entry.getValue()) {
                contestantJids.add(contestant.getUserJid());
                contestantStartTimesMap.get(entry.getKey()).put(contestant.getUserJid(), contestant.getContestStartTime());
            }
        }

        Map<String, Integer> pointsMap = new HashMap<>();
        if (scoreboardState.getProblemPoints().isPresent()) {
            for (int i = 0; i < scoreboardState.getProblemJids().size(); i++) {
                pointsMap.putIfAbsent(
                        scoreboardState.getProblemJids().get(i),
                        scoreboardState.getProblemPoints().get().get(i));
            }
        } else {
            scoreboardState.getProblemJids().forEach(p -> pointsMap.putIfAbsent(p, 0));
        }

        Map<String, List<Submission>> submissionsMap = new HashMap<>();
        contestantJids.forEach(c -> submissionsMap.put(c, new ArrayList<>()));
        programmingSubmissions.forEach(s -> submissionsMap.get(s.getUserJid()).add(s));

        Optional<GcjScoreboardIncrementalContent> maybeIncrementalContent =
                incrementalContent.map(content -> (GcjScoreboardIncrementalContent) content);

        Map<String, Map<String, Integer>> attemptsMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getAttemptsMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, Long>> penaltyMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getPenaltyMapsByContestantJid()).orElse(emptyMap()));
        Map<String, Map<String, GcjScoreboardProblemState>> problemStateMapsByContestantJid = new HashMap<>(
                maybeIncrementalContent.map(c -> c.getProblemStateMapsByContestantJid()).orElse(emptyMap()));

        Optional<Long> nextLastSubmissionId = maybeIncrementalContent.flatMap(ic -> ic.getLastSubmissionId());
        for (Submission s : programmingSubmissions) {
            if (!s.getLatestGrading().isPresent() || s.getLatestGrading().get().getVerdict() == Verdict.PENDING) {
                break;
            }
            nextLastSubmissionId = Optional.of(s.getId());
        }

        List<GcjScoreboardEntry> entries = new ArrayList<>();
        for (String contestantJid : submissionsMap.keySet()) {
            Map<String, Integer> attemptsMap = new HashMap<>(
                    attemptsMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, Long> penaltyMap = new HashMap<>(
                    penaltyMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));
            Map<String, GcjScoreboardProblemState> problemStateMap = new HashMap<>(
                    problemStateMapsByContestantJid.getOrDefault(contestantJid, emptyMap()));

            Profile contestantProfile = profilesMap.get(contestantJid);
            Optional<UserRating> maybeUserRating = contestantProfile.getRating();
            UserRating userRating = maybeUserRating.orElseGet(
                    () -> new UserRating.Builder().publicRating(0).hiddenRating(0).build());

            problemJids.forEach(p -> {
                attemptsMap.putIfAbsent(p, 0);
                penaltyMap.putIfAbsent(p, 0L);
                problemStateMap.putIfAbsent(p, GcjScoreboardProblemState.NOT_ACCEPTED);
            });

            for (Submission submission : submissionsMap.get(contestantJid)) {
                String problemJid = submission.getProblemJid();

                if (submission.getTime().isBefore(contestFreezeTimesMap.getOrDefault(submission.getContainerJid(), Instant.MAX))) {
                    Verdict verdict = submission.getLatestGrading().get().getVerdict();
                    if (verdict.equals(Verdict.PENDING)) {
                        continue;
                    }

                    if (!isAccepted(problemStateMap.get(problemJid)) || !verdict.equals(Verdict.ACCEPTED)) {
                        attemptsMap.put(problemJid, attemptsMap.get(problemJid) + 1);
                    }

                    if (!isAccepted(problemStateMap.get(problemJid)) && verdict.equals(Verdict.ACCEPTED)) {
                        long penalty = computePenalty(
                                submission.getTime(),
                                contestantStartTimesMap.get(submission.getContainerJid()).get(contestantJid),
                                contestBeginTimesMap.get(submission.getContainerJid()));
                        penaltyMap.put(problemJid, convertPenaltyToMinutes(penalty));
                        problemStateMap.put(problemJid, GcjScoreboardProblemState.ACCEPTED);
                    }
                } else {
                    if (!isAccepted(problemStateMap.get(problemJid))) {
                        problemStateMap.put(problemJid, GcjScoreboardProblemState.FROZEN);
                    }
                }

                if (submission.getId() <= nextLastSubmissionId.orElse(Long.MAX_VALUE)) {
                    attemptsMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(attemptsMap));
                    penaltyMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(penaltyMap));
                    problemStateMapsByContestantJid.put(contestantJid, ImmutableMap.copyOf(problemStateMap));
                }
            }

            entries.add(new GcjScoreboardEntry.Builder()
                    .rank(0)
                    .contestantJid(contestantJid)
                    .contestantUsername(contestantProfile.getUsername())
                    .contestantRating(userRating.getPublicRating())
                    .totalPoints(problemJids.stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToInt(p -> scoreboardState.getProblemPoints().map($ -> pointsMap.get(p)).orElse(0))
                            .sum())
                    .totalPenalties(penaltyMap.keySet().stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToLong(penaltyMap::get)
                            .max()
                            .orElse(0)
                            + penaltyMap.keySet().stream()
                            .filter(p -> isAccepted(problemStateMap.get(p)))
                            .mapToLong(p -> (attemptsMap.get(p) - 1)
                                    * gcjStyleModuleConfig.getWrongSubmissionPenalty())
                            .sum()
                    )
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

        return new ScoreboardProcessResult.Builder()
                .entries(entries)
                .incrementalContent(new GcjScoreboardIncrementalContent.Builder()
                        .lastSubmissionId(nextLastSubmissionId)
                        .attemptsMapsByContestantJid(attemptsMapsByContestantJid)
                        .penaltyMapsByContestantJid(penaltyMapsByContestantJid)
                        .problemStateMapsByContestantJid(problemStateMapsByContestantJid)
                        .build())
                .build();
    }

    @Override
    public GcjScoreboardEntry clearEntryRank(ScoreboardEntry entry) {
        return new GcjScoreboardEntry.Builder()
                .from((GcjScoreboardEntry) entry)
                .rank(-1)
                .build();
    }

    private List<GcjScoreboardEntry> sortEntriesAndAssignRanks(List<GcjScoreboardEntry> entries) {
        ScoreboardEntryComparator<GcjScoreboardEntry> comparator = new StandardGcjScoreboardEntryComparator();
        entries.sort(comparator);

        List<GcjScoreboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (int i = 0; i < entries.size(); i++) {
            if (i != 0 && comparator.compareWithoutTieBreakerForEqualRanks(entries.get(i), entries.get(i - 1)) != 0) {
                currentRank = i + 1;
            }
            result.add(new GcjScoreboardEntry.Builder().from(entries.get(i)).rank(currentRank).build());
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

    private boolean isAccepted(GcjScoreboardProblemState problemState) {
        return problemState.equals(GcjScoreboardProblemState.ACCEPTED);
    }
}
