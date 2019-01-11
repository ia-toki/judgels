package org.iatoki.judgels.jerahmeel.problemset.problem;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProblemSetProblemModel.class)
public abstract class ProblemSetProblemModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<ProblemSetProblemModel, Long> id;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> problemSetJid;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> problemJid;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> problemSecret;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> alias;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> type;
	public static volatile SingularAttribute<ProblemSetProblemModel, String> status;
}
