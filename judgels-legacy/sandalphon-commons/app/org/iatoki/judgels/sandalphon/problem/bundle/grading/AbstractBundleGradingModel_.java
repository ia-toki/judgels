package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractBundleGradingModel.class)
public abstract class AbstractBundleGradingModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractBundleGradingModel, String> submissionJid;
        public static volatile SingularAttribute<AbstractBundleGradingModel, Double> score;
        public static volatile SingularAttribute<AbstractBundleGradingModel, String> details;
}
