package org.iatoki.judgels.sandalphon.problem.base;

import java.util.Map;
import play.data.validation.Constraints;

public final class ProblemEditForm {

    @Constraints.Required
    @Constraints.Pattern("^[a-z0-9]+(-[a-z0-9]+)*$")
    public String slug;

    public String additionalNote;

    public String writerUsernames;

    public String developerUsernames;

    public String testerUsernames;

    public String editorialistUsernames;

    public Map<String, String> tags;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getWriterUsernames() {
        return writerUsernames;
    }

    public void setWriterUsernames(String writerUsernames) {
        this.writerUsernames = writerUsernames;
    }

    public String getDeveloperUsernames() {
        return developerUsernames;
    }

    public void setDeveloperUsernames(String developerUsernames) {
        this.developerUsernames = developerUsernames;
    }

    public String getTesterUsernames() {
        return testerUsernames;
    }

    public void setTesterUsernames(String testerUsernames) {
        this.testerUsernames = testerUsernames;
    }

    public String getEditorialistUsernames() {
        return editorialistUsernames;
    }

    public void setEditorialistUsernames(String editorialistUsernames) {
        this.editorialistUsernames = editorialistUsernames;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
