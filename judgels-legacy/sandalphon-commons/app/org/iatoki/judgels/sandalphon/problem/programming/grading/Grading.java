package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.gabriel.api.Verdict;

public final class Grading {

    private final long id;
    private final String jid;
    private final Verdict verdict;
    private final int score;
    private final String details;

    public Grading(long id, String jid, Verdict verdict, int score, String details) {
        this.id = id;
        this.jid = jid;
        this.verdict = verdict;
        this.score = score;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public int getScore() {
        return score;
    }

    public String getDetails() {
        return details;
    }
}
