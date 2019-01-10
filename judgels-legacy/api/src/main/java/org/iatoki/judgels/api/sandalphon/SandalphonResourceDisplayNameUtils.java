package org.iatoki.judgels.api.sandalphon;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.stream.Collectors;

public final class SandalphonResourceDisplayNameUtils {

    private SandalphonResourceDisplayNameUtils() {
        // prevent instantiation
    }

    public static Map<String, String> buildTitlesMap(Map<String, String> rawDisplayNamesMap, String language) {
        return rawDisplayNamesMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> parseTitleByLanguage(e.getValue(), language)));
    }

    public static Map<String, String> buildSlugsMap(Map<String, String> rawDisplayNamesMap) {
        return rawDisplayNamesMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> parseSlugByLanguage(e.getValue())));
    }

    public static String parseTitleByLanguage(String rawDisplayName, String language) {
        try {
            SandalphonResourceDisplayName resourceDisplayName = new Gson().fromJson(rawDisplayName, SandalphonResourceDisplayName.class);
            if (!resourceDisplayName.getTitlesByLanguage().containsKey(language)) {
                return resourceDisplayName.getTitlesByLanguage().get(resourceDisplayName.getDefaultLanguage());
            }
            return resourceDisplayName.getTitlesByLanguage().get(language);
        } catch (JsonSyntaxException e) {
            return rawDisplayName;
        }
    }

    public static String parseSlugByLanguage(String rawDisplayName) {
        try {
            SandalphonResourceDisplayName resourceDisplayName = new Gson().fromJson(rawDisplayName, SandalphonResourceDisplayName.class);
            return resourceDisplayName.getSlug();
        } catch (JsonSyntaxException e) {
            return rawDisplayName;
        }
    }
}
