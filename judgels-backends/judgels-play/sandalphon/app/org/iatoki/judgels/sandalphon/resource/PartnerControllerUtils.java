package org.iatoki.judgels.sandalphon.resource;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.Set;

public final class PartnerControllerUtils {

    private PartnerControllerUtils() {
        // prevent instantiation
    }

    public static Set<String> splitByComma(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return Sets.newHashSet(s.split(","));
    }

    public static String combineByComma(Set<String> list) {
        if (list == null) {
            return null;
        }
        return Joiner.on(",").join(list);
    }
}
