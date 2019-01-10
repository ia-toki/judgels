package org.iatoki.judgels.sandalphon.problem.programming.grading;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractProgrammingGradingModel.class)
public abstract class AbstractProgrammingGradingModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractProgrammingGradingModel, String> submissionJid;
        public static volatile SingularAttribute<AbstractProgrammingGradingModel, String> verdictCode;
        public static volatile SingularAttribute<AbstractProgrammingGradingModel, String> verdictName;
        public static volatile SingularAttribute<AbstractProgrammingGradingModel, Integer> score;
        public static volatile SingularAttribute<AbstractProgrammingGradingModel, String> details;
}
