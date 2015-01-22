package org.iatoki.judgels.gabriel.blackbox;

public final class Subtask {
    private final int points;
    private final String param;

    public Subtask(int points, String param) {
        this.points = points;
        this.param = param;
    }

    public int getPoints() {
        return points;
    }

    public String getParam() {
        return param;
    }
}
