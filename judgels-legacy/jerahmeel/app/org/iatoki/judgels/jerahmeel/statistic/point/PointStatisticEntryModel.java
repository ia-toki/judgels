package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_point_statistic_entry")
public class PointStatisticEntryModel extends Model {
    public String pointStatisticJid;

    public String userJid;

    public double totalPoints;

    public long totalProblems;
}
