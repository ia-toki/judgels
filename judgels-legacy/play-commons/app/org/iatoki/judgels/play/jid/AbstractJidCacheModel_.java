package org.iatoki.judgels.play.jid;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractJidCacheModel.class)
public abstract class AbstractJidCacheModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<AbstractJidCacheModel, Long> id;
    public static volatile SingularAttribute<AbstractJidCacheModel, String> jid;
    public static volatile SingularAttribute<AbstractJidCacheModel, String> displayName;
}