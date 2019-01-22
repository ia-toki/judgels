package org.iatoki.judgels.jerahmeel.course;

import play.data.validation.Constraints;

public final class CourseUpsertForm {

    @Constraints.Required
    public String name;

    public String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
