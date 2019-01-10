package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProblemPartnerModel.class)
public final class ProblemPartnerModel_ extends AbstractModel_{

    public static volatile SingularAttribute<ProblemPartnerModel, Long> id;
    public static volatile SingularAttribute<ProblemPartnerModel, String> problemJid;
    public static volatile SingularAttribute<ProblemPartnerModel, String> userJid;
    public static volatile SingularAttribute<ProblemPartnerModel, String> baseConfig;
    public static volatile SingularAttribute<ProblemPartnerModel, String> childConfig;
}
