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

        String prefix = "JID" + modelClass.getAnnotation(JidPrefix.class).value();
        String suffix = GEN.generate(20);

        return prefix + suffix;
    }
}
