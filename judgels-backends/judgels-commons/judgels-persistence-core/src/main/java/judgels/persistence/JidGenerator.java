package judgels.persistence;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.text.RandomStringGenerator;

public class JidGenerator {
    private static final RandomStringGenerator GEN;

    static {
        UniformRandomProvider rng = RandomSource.create(RandomSource.MT);
        GEN = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .usingRandom(rng::nextInt)
                .build();
    }

    private JidGenerator() {}

    public static <M extends JudgelsModel> String newJid(Class<M> modelClass) {
        if (!modelClass.isAnnotationPresent(JidPrefix.class)) {
            throw new IllegalArgumentException(modelClass.getSimpleName() + " must have @JidPrefix annotation");
        }

        return generateJid(modelClass.getAnnotation(JidPrefix.class).value());
    }

    public static <M extends JudgelsModel> String newChildJid(Class<M> modelClass, int childIndex) {
        if (!modelClass.isAnnotationPresent(JidChildPrefixes.class)) {
            throw new IllegalStateException(modelClass.getSimpleName() + " must have JidChildPrefixes annotation");
        }

        String[] codes = modelClass.getAnnotation(JidChildPrefixes.class).value();
        if (childIndex >= codes.length) {
            String message = "The " + childIndex + "-th child of " + modelClass.getSimpleName() + " does not exist";
            throw new IllegalStateException(message);
        }

        return generateJid(codes[childIndex]);
    }

    public static String generateJid(String code) {
        String prefix = "JID" + code;
        String suffix = GEN.generate(20);

        return prefix + suffix;
    }
}
