package judgels.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JidGeneratorTests {
    @Test void generates_new_jid() {
        String jid = JidGenerator.newJid(ExampleModel.class);
        assertThat(jid).containsPattern("^JIDEXAM[a-zA-Z0-9]{20}$");

        String anotherJid = JidGenerator.newJid(ExampleModel.class);
        assertThat(jid).isNotEqualTo(anotherJid);
    }

    @JidPrefix("EXAM")
    private static class ExampleModel extends JudgelsModel {}
}
