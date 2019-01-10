package org.iatoki.judgels.sandalphon.problem.programming.submission;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractProgrammingSubmissionModel.class)
public abstract class AbstractProgrammingSubmissionModel_ extends AbstractModel_ {

        public static volatile SingularAttribute<AbstractProgrammingSubmissionModel, String> problemJid;
        public static volatile SingularAttribute<AbstractProgrammingSubmissionModel, String> containerJid;
        public static volatile SingularAttribute<AbstractProgrammingSubmissionModel, String> gradingEngine;
        public static volatile SingularAttribute<AbstractProgrammingSubmissionModel, String> gradingLanguage;
}
