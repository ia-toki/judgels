package judgels.uriel.api.contest.scoreboard;

public interface ScoreboardEntry {
    int getRank();
    String getContestantJid();
    boolean hasSubmission();
}
