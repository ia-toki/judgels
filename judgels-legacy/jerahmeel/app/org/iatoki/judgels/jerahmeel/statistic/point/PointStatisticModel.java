package org.iatoki.judgels.jerahmeel.statistic.point;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Entity;
import java.time.Instant;

@Entity(name = "jerahmeel_point_statistic")
@JidPrefix("POST")
public class PointStatisticModel extends JudgelsModel {

    public Instant time;
}
