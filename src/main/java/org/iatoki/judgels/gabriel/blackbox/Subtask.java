package org.iatoki.judgels.gabriel.blackbox;

public final class Subtask {
    private final double points;
    private final String param;

    public Subtask(double points, String param) {
        this.points = points;
        this.param = param;
    }

    public double getPoints() {
        return points;
    }

    public String getParam() {
        return param;
    }
}
