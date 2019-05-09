package judgels.gabriel.languages;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GradingLanguageRegistryTests {
    @Test
    void get() {
        assertThat(GradingLanguageRegistry.getInstance().get("Cpp").getName()).isEqualTo("C++");
        assertThat(GradingLanguageRegistry.getInstance().get("C").getName()).isEqualTo("C");
    }

    @Test
    void getNamesMap() {
        assertThat(GradingLanguageRegistry.getInstance().getNamesMap()).containsEntry("Cpp", "C++");
        assertThat(GradingLanguageRegistry.getInstance().getNamesMap()).containsEntry("C", "C");
    }
}
