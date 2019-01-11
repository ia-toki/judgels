package org.iatoki.judgels.jerahmeel.curriculum.course;

public final class CurriculumCourseWithProgress {

    private final CurriculumCourse curriculumCourse;
    private final CourseProgress courseProgress;
    private final long completedChapters;
    private final long totalChapters;
    private final double totalScores;

    public CurriculumCourseWithProgress(CurriculumCourse curriculumCourse, CourseProgress courseProgress, long completedChapters, long totalChapters, double totalScores) {
        this.curriculumCourse = curriculumCourse;
        this.courseProgress = courseProgress;
        this.completedChapters = completedChapters;
        this.totalChapters = totalChapters;
        this.totalScores = totalScores;
    }

    public CurriculumCourse getCurriculumCourse() {
        return curriculumCourse;
    }

    public CourseProgress getCourseProgress() {
        return courseProgress;
    }

    public long getCompletedChapters() {
        return completedChapters;
    }

    public long getTotalChapters() {
        return totalChapters;
    }

    public double getTotalScores() {
        return totalScores;
    }
}
