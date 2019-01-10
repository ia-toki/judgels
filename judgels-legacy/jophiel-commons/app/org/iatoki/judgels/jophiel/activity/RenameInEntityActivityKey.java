package org.iatoki.judgels.jophiel.activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public abstract class RenameInEntityActivityKey implements ActivityKey {

    private String entity;

    private String entityJid;

    private String entityFromName;

    private String entityToName;

    private String refEntity;

    private String refEntityJid;

    private String refEntityName;

    public final ActivityKey construct(String refEntity, String refEntityJid, String refEntityName, String entity, String entityJid, String entityFromName, String entityToName) {
        this.refEntity = refEntity;
        this.refEntityJid = refEntityJid;
        this.refEntityName = refEntityName;
        this.entity = entity;
        this.entityJid = entityJid;
        this.entityFromName = entityFromName;
        this.entityToName = entityToName;

        return this;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityJid() {
        return entityJid;
    }

    public String getEntityFromName() {
        return entityFromName;
    }

    public String getEntityToName() {
        return entityToName;
    }

    public String getRefEntity() {
        return refEntity;
    }

    public String getRefEntityJid() {
        return refEntityJid;
    }

    public String getRefEntityName() {
        return refEntityName;
    }

    @Override
    public String toJsonString() {
        return new Gson().toJson(this, RenameInEntityActivityKey.class);
    }

    @Override
    public ActivityKey fromJson(String json) {
        Map<String, String> fields = new Gson().fromJson(json, new TypeToken<Map<String, String>>() { }.getType());

        return this.construct(fields.get("entity"), fields.get("entityJid"), fields.get("entityFromName"), fields.get("entityToName"), fields.get("refEntity"), fields.get("refEntityJid"), fields.get("refEntityName"));
    }
}
