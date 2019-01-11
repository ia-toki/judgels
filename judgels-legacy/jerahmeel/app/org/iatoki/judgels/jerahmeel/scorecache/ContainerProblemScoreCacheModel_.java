package org.iatoki.judgels.jerahmeel.scorecache;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ContainerProblemScoreCacheModel.class)
public abstract class ContainerProblemScoreCacheModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<ContainerProblemScoreCacheModel, Long> id;
	public static volatile SingularAttribute<ContainerProblemScoreCacheModel, String> containerJid;
	public static volatile SingularAttribute<ContainerProblemScoreCacheModel, String> problemJid;
	public static volatile SingularAttribute<ContainerProblemScoreCacheModel, String> userJid;
	public static volatile SingularAttribute<ContainerProblemScoreCacheModel, Double> score;
}
