package org.iatoki.judgels.jerahmeel.chapter.problem;

public final class ChapterProblemWithProgress {

    private final ChapterProblem chapterProblem;
    private final ProblemProgress problemProgress;
    private final double score;

    public ChapterProblemWithProgress(ChapterProblem chapterProblem, ProblemProgress problemProgress, double score) {
        this.chapterProblem = chapterProblem;
        this.problemProgress = problemProgress;
        this.score = score;
    }

    public ChapterProblem getChapterProblem() {
        return chapterProblem;
    }

    public ProblemProgress getProblemProgress() {
        return problemProgress;
    }

    public double getScore() {
        return score;
    }
}
