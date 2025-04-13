package judgels.sandalphon.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import judgels.persistence.Model;

@SuppressWarnings("checkstyle:visibilitymodifier")
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
