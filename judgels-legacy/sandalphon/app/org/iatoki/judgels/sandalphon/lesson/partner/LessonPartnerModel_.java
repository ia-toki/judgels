package org.iatoki.judgels.sandalphon.lesson.partner;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LessonPartnerModel.class)
public final class LessonPartnerModel_ extends AbstractModel_{

    public static volatile SingularAttribute<LessonPartnerModel, Long> id;
    public static volatile SingularAttribute<LessonPartnerModel, String> lessonJid;
    public static volatile SingularAttribute<LessonPartnerModel, String> userJid;
    public static volatile SingularAttribute<LessonPartnerModel, String> config;
}
