package org.iatoki.judgels.sandalphon.lesson.partner;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sandalphon_lesson_partner")
public final class LessonPartnerModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String lessonJid;

    public String userJid;

    @Column(columnDefinition = "TEXT")
    public String config;
}
