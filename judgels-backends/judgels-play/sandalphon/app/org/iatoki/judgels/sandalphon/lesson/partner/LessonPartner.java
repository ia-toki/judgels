package org.iatoki.judgels.sandalphon.lesson.partner;

import com.google.gson.Gson;

public final class LessonPartner {

    private final long id;
    private final String lessonJid;
    private final String partnerJid;
    private final String config;

    public LessonPartner(long id, String lessonJid, String partnerJid, String config) {
        this.id = id;
        this.lessonJid = lessonJid;
        this.partnerJid = partnerJid;
        this.config = config;
    }

    public long getId() {
        return id;
    }

    public String getLessonJid() {
        return lessonJid;
    }

    public String getPartnerJid() {
        return partnerJid;
    }

    public LessonPartnerConfig getConfig() {
        return new Gson().fromJson(config, LessonPartnerConfig.class);
    }
}
