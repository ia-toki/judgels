package org.iatoki.judgels.sandalphon.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.icu.util.ULocale;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class WorldLanguageRegistry {

    private static final WorldLanguageRegistry INSTANCE = new WorldLanguageRegistry();

    private Map<String, String> registry;

    private WorldLanguageRegistry() {
        List<ULocale> locales = Arrays.asList(ULocale.getAvailableLocales()).stream().filter(l -> l.toLanguageTag().contains("-")).collect(Collectors.toList());
        Collections.sort(locales, (ULocale l1, ULocale l2) -> l1.getDisplayLanguage().compareTo(l2.getDisplayLanguage()));

        this.registry = Maps.newLinkedHashMap();
        for (ULocale locale : locales) {
            this.registry.put(locale.toLanguageTag(), locale.getDisplayLanguage() + " (" + locale.toLanguageTag() + ")");
        }
    }

    public static WorldLanguageRegistry getInstance() {
        return INSTANCE;
    }

    public Map<String, String> getLanguages() {
        return ImmutableMap.copyOf(registry);
    }

    public String getDisplayLanguage(String languageCode) {
        return registry.get(languageCode);
    }
}
