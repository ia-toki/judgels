package org.iatoki.judgels.jophiel.activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public abstract class OneEntityActivityKey implements ActivityKey {

    private String entity;

    private String entityJid;

    private String entityName;

    public final OneEntityActivityKey construct(String entity, String entityJid, String entityName) {
        this.entity = entity;
        this.entityJid = entityJid;
        this.entityName = entityName;

        return this;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityJid() {
        return entityJid;
    }

    public String getEntityName() {
        return entityName;
    }

    @Override
    public String toJsonString() {
        return new Gson().toJson(this, OneEntityActivityKey.class);
    }

    @Override
    public ActivityKey fromJson(String json) {
        Map<String, String> fields = new Gson().fromJson(json, new TypeToken<Map<String, String>>() { }.getType());

        return this.construct(fields.get("entity"), fields.get("entityJid"), fields.get("entityName"));
    }
}
