package org.iatoki.judgels.jerahmeel.course.chapter;

public final class CourseChapterWithProgress {

    private final CourseChapter courseChapter;
    private final ChapterProgress chapterProgress;
    private final long solvedProblems;
    private final long totalProblems;
    private final double totalScores;

    public CourseChapterWithProgress(CourseChapter courseChapter, ChapterProgress chapterProgress, long solvedProblems, long totalProblems, double totalScores) {
        this.courseChapter = courseChapter;
        this.chapterProgress = chapterProgress;
        this.solvedProblems = solvedProblems;
        this.totalProblems = totalProblems;
        this.totalScores = totalScores;
    }

    public CourseChapter getCourseChapter() {
        return courseChapter;
    }

    public ChapterProgress getChapterProgress() {
        return chapterProgress;
    }

    public long getSolvedProblems() {
        return solvedProblems;
    }

    public long getTotalProblems() {
        return totalProblems;
    }

    public double getTotalScores() {
        return totalScores;
    }
}
