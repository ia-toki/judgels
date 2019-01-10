package org.iatoki.judgels.jophiel.user;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractUserModel.class)
public abstract class AbstractUserModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<AbstractUserModel, Long> id;
    public static volatile SingularAttribute<AbstractUserModel, String> userJid;
    public static volatile SingularAttribute<AbstractUserModel, String> accessToken;
    public static volatile SingularAttribute<AbstractUserModel, String> refreshToken;
    public static volatile SingularAttribute<AbstractUserModel, String> idToken;
    public static volatile SingularAttribute<AbstractUserModel, String> expirationTime;
}