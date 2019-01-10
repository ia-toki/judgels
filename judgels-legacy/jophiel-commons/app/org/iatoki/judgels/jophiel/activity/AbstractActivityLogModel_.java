package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractActivityLogModel.class)
public abstract class AbstractActivityLogModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractActivityLogModel, Long> id;
        public static volatile SingularAttribute<AbstractActivityLogModel, String> username;
        public static volatile SingularAttribute<AbstractActivityLogModel, String> keyAction;
        public static volatile SingularAttribute<AbstractActivityLogModel, String> parameters;
        public static volatile SingularAttribute<AbstractActivityLogModel, Long> time;
}
