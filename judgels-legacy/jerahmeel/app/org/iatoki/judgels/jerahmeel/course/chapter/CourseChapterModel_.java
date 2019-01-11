package org.iatoki.judgels.jerahmeel.course.chapter;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CourseChapterModel.class)
public abstract class CourseChapterModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<CourseChapterModel, Long> id;
	public static volatile SingularAttribute<CourseChapterModel, String> courseJid;
	public static volatile SingularAttribute<CourseChapterModel, String> chapterJid;
	public static volatile SingularAttribute<CourseChapterModel, String> alias;
}
