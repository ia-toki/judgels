package org.iatoki.judgels.sandalphon.lesson.partner;

import judgels.persistence.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sandalphon_lesson_partner")
public final class LessonPartnerModel extends Model {
    public String lessonJid;

    public String userJid;

    @Column(columnDefinition = "TEXT")
    public String config;
}
