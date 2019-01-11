package org.iatoki.judgels.jerahmeel.statistic.problemscore;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProblemScoreStatisticEntryModel.class)
public abstract class ProblemScoreStatisticEntryModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<ProblemScoreStatisticEntryModel, Long> id;
	public static volatile SingularAttribute<ProblemScoreStatisticEntryModel, String> problemScoreStatisticJid;
	public static volatile SingularAttribute<ProblemScoreStatisticEntryModel, String> userJid;
	public static volatile SingularAttribute<ProblemScoreStatisticEntryModel, Double> score;
	public static volatile SingularAttribute<ProblemScoreStatisticEntryModel, Long> time;
}
