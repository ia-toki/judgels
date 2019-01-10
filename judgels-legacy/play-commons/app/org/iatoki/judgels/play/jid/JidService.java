package org.iatoki.judgels.play.jid;

import org.apache.commons.lang3.RandomStringUtils;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

public final class JidService {

    private static JidService INSTANCE;

    private JidService() {
        // prevent instantiation
    }

    public static JidService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JidService();
        }
        return INSTANCE;
    }

    public <M extends AbstractJudgelsModel> String generateNewChildJid(Class<M> modelClass, int childIndex) {
        if (!modelClass.isAnnotationPresent(JidChildPrefixes.class)) {
            throw new IllegalStateException("Model " + modelClass.getSimpleName() + " must have JidChildPrefixes annotation");
        }

        String[] codes = modelClass.getAnnotation(JidChildPrefixes.class).value();
        if (childIndex >= codes.length) {
            throw new IllegalStateException("The " + childIndex + "-th child of " + modelClass.getSimpleName() + " does not exist");
        }

        return generateNewJid(codes[childIndex]);
    }

    public String parsePrefix(String jid) {
        int jidLength = "JID".length();
        int prefixLength = 4;

        return jid.substring(jidLength, jidLength + prefixLength);
    }

    public <M extends AbstractJudgelsModel> String generateNewJid(Class<M> modelClass) {
        if (!modelClass.isAnnotationPresent(JidPrefix.class)) {
            throw new IllegalStateException("Model " + modelClass.getSimpleName() + " must have JidPrefix annotation");
        }

        return generateNewJid(modelClass.getAnnotation(JidPrefix.class).value());
    }

    public String generateNewJid(String code) {
        String prefix = "JID" + code;
        String suffix = RandomStringUtils.randomAlphanumeric(20);

        return prefix + suffix;
    }
}
