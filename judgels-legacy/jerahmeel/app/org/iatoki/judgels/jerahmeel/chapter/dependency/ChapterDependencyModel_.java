package org.iatoki.judgels.jerahmeel.chapter.dependency;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ChapterDependencyModel.class)
public abstract class ChapterDependencyModel_ extends AbstractModel_ {

	public static volatile SingularAttribute<ChapterDependencyModel, Long> id;
	public static volatile SingularAttribute<ChapterDependencyModel, String> chapterJid;
	public static volatile SingularAttribute<ChapterDependencyModel, String> dependedChapterJid;
}
