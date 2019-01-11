package org.iatoki.judgels.jerahmeel.problemset.problem;

public final class ProblemSetProblem {

    private final long id;
    private final String problemSetJid;
    private final String problemJid;
    private final String problemSecret;
    private final String alias;
    private final ProblemSetProblemType type;
    private final ProblemSetProblemStatus status;

    public ProblemSetProblem(long id, String problemSetJid, String problemJid, String problemSecret, String alias, ProblemSetProblemType type, ProblemSetProblemStatus status) {
        this.id = id;
        this.problemSetJid = problemSetJid;
        this.problemJid = problemJid;
        this.problemSecret = problemSecret;
        this.alias = alias;
        this.type = type;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getProblemSetJid() {
        return problemSetJid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getProblemSecret() {
        return problemSecret;
    }

    public String getAlias() {
        return alias;
    }

    public ProblemSetProblemType getType() {
        return type;
    }

    public ProblemSetProblemStatus getStatus() {
        return status;
    }
}
