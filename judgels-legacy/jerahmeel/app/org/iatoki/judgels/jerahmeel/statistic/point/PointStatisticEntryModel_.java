package org.iatoki.judgels.jerahmeel.statistic.point;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PointStatisticEntryModel.class)
public abstract class PointStatisticEntryModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<PointStatisticEntryModel, Long> id;
	public static volatile SingularAttribute<PointStatisticEntryModel, String> pointStatisticJid;
	public static volatile SingularAttribute<PointStatisticEntryModel, String> userJid;
	public static volatile SingularAttribute<PointStatisticEntryModel, Double> totalPoints;
	public static volatile SingularAttribute<PointStatisticEntryModel, Long> totalProblems;
}
