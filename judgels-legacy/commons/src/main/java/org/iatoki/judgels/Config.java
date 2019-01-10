package org.iatoki.judgels;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigValue;

import java.util.Map;

public final class Config {
    private final com.typesafe.config.Config config;

    public Config(com.typesafe.config.Config config) {
        this.config = config;
    }

    public String requireString(String key) {
        return config.getString(key);
    }

    public String getString(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getString(key);
    }

    public int requireInt(String key) {
        return config.getInt(key);
    }

    public Integer getInt(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getInt(key);
    }

    public boolean requireBoolean(String key) {
        return config.getBoolean(key);
    }

    public Boolean getBoolean(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getBoolean(key);
    }

    public <T> Map<String, T> requireMap(String key, Class<T> clazz) {
        ImmutableMap.Builder<String, T> mapBuilder = ImmutableMap.builder();
        com.typesafe.config.Config tempConfig = config.getConfig(key);
        for (Map.Entry<String, ConfigValue> entrySet : tempConfig.entrySet()) {
            mapBuilder.put(entrySet.getKey(), clazz.cast(entrySet.getValue().unwrapped()));
        }

        return mapBuilder.build();
    }

    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        if (!config.hasPath(key)) {
            return null;
        }

        return requireMap(key, clazz);
    }
}
