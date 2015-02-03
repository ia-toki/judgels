package org.iatoki.judgels.gabriel.blackbox;

public final class Subtask {
    private final int id;
    private final int points;
    private final String param;

    public Subtask(int id, int points, String param) {
        this.id = id;
        this.points = points;
        this.param = param;
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public String getParam() {
        return param;
    }
}
