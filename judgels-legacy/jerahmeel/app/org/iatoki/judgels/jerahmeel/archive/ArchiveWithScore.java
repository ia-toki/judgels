package org.iatoki.judgels.jerahmeel.archive;

public final class ArchiveWithScore {

    private final Archive archive;
    private final double score;

    public ArchiveWithScore(Archive archive, double score) {
        this.archive = archive;
        this.score = score;
    }

    public Archive getArchive() {
        return archive;
    }

    public double getScore() {
        return score;
    }
}
