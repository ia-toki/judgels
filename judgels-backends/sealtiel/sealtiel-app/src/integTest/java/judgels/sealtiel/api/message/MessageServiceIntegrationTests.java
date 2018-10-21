package judgels.sealtiel.api.message;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.sealtiel.api.AbstractServiceIntegrationTests;
import judgels.service.api.client.BasicAuthHeader;
import org.junit.jupiter.api.Test;

class MessageServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static final BasicAuthHeader AUTH_HEADER_1 = BasicAuthHeader.of(CLIENT_1);
    private static final BasicAuthHeader AUTH_HEADER_2 = BasicAuthHeader.of(CLIENT_2);

    @Test
    void end_to_end_flow() {
        MessageService messageService = createService(MessageService.class);
        assertThat(messageService.receiveMessage(AUTH_HEADER_1)).isEmpty();

        messageService.sendMessage(AUTH_HEADER_2, new MessageData.Builder()
                .targetJid(CLIENT_1.getJid())
                .type("GRADING_REQUEST")
                .content("grading content")
                .build());

        Message message = messageService.receiveMessage(AUTH_HEADER_1).get();

        assertThat(message.getSourceJid()).isEqualTo(CLIENT_2.getJid());
        assertThat(message.getType()).isEqualTo("GRADING_REQUEST");
        assertThat(message.getContent()).isEqualTo("grading content");

        messageService.confirmMessage(AUTH_HEADER_1, message.getId());
        assertThat(messageService.receiveMessage(AUTH_HEADER_1)).isEmpty();
    }
}
