package org.iatoki.judgels.jophiel.activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public abstract class NoEntityActivityKey implements ActivityKey {

    public final NoEntityActivityKey construct() {
        return this;
    }

    @Override
    public String toJsonString() {
        return new Gson().toJson(this, NoEntityActivityKey.class);
    }

    @Override
    public ActivityKey fromJson(String json) {
        Map<String, String> fields = new Gson().fromJson(json, new TypeToken<Map<String, String>>() { }.getType());

        return this.construct();
    }
}
