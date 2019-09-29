package org.iatoki.judgels.sandalphon.lesson.partner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "sandalphon_lesson_partner")
@Table(indexes = {@Index(columnList = "lessonJid,userJid", unique = true)})
public final class LessonPartnerModel extends Model {
    @Column(nullable = false)
    public String lessonJid;

    @Column(nullable = false)
    public String userJid;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String config;
}
