package org.iatoki.judgels.jerahmeel.curriculum.course;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "jerahmeel_curriculum_course")
@Table(indexes = {
        @Index(columnList = "curriculumJid,courseJid", unique = true),
        @Index(columnList = "curriculumJid,alias", unique = true)})
public final class CurriculumCourseModel extends Model {
    @Column(nullable = false)
    public String curriculumJid;

    @Column(nullable = false)
    public String courseJid;

    @Column(nullable = false)
    public String alias;
}
