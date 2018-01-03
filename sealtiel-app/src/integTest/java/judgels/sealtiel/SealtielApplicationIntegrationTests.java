package judgels.sealtiel;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.sealtiel.SealtielApplicationExtension.CLIENT_1;
import static judgels.sealtiel.SealtielApplicationExtension.CLIENT_2;
import static judgels.sealtiel.SealtielApplicationExtension.createAdminWebTarget;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.GenericType;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SealtielApplicationExtension.class)
class SealtielApplicationIntegrationTests {
    private static final BasicAuthHeader AUTH_HEADER_1 = BasicAuthHeader.of(CLIENT_1);
    private static final BasicAuthHeader AUTH_HEADER_2 = BasicAuthHeader.of(CLIENT_2);

    @Test void messaging_flow() {
        MessageService messageService = SealtielApplicationExtension.createService(MessageService.class);
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

    @Test void rabbitmq_healthcheck() {
        Map<String, Map<String, Boolean>> result = createAdminWebTarget()
                .path("/healthcheck")
                .request(APPLICATION_JSON)
                .get()
                .readEntity(new GenericType<HashMap<String, Map<String, Boolean>>>() {});

        assertThat(result.get("rabbitmq").get("healthy")).isTrue();
    }
}
