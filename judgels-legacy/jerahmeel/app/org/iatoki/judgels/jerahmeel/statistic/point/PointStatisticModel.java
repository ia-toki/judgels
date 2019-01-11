package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_point_statistic")
@JidPrefix("POST")
public class PointStatisticModel extends AbstractJudgelsModel {

    public long time;
}
