package org.iatoki.judgels.jerahmeel.user.item;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserItemModel.class)
public abstract class UserItemModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<UserItemModel, Long> id;
	public static volatile SingularAttribute<UserItemModel, String> userJid;
	public static volatile SingularAttribute<UserItemModel, String> itemJid;
	public static volatile SingularAttribute<UserItemModel, String> status;
}
