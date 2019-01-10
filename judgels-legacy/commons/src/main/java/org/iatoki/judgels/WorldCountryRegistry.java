package org.iatoki.judgels;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class WorldCountryRegistry {

    private static final WorldCountryRegistry INSTANCE = new WorldCountryRegistry();

    private final List<String> registry;

    private WorldCountryRegistry() {
        registry = Arrays.asList(Locale.getISOCountries()).stream().map(c -> new Locale("", c).getDisplayCountry()).collect(Collectors.toList());
        Collections.sort(registry);
    }

    public static WorldCountryRegistry getInstance() {
        return INSTANCE;
    }

    public List<String> getCountries() {
        return ImmutableList.copyOf(registry);
    }
}
