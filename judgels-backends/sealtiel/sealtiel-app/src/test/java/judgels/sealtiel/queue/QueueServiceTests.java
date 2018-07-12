package judgels.sealtiel.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import judgels.sealtiel.api.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

class QueueServiceTests {
    private static final String CLIENT_JID_1 = "JIDSECL-1";
    private static final String CLIENT_JID_2 = "JIDSECL-2";

    private @Mock Queue queue;
    private @Mock ObjectMapper objectMapper;
    private @Mock QueueChannel queueChannel;
    private QueueService queueService;

    @BeforeEach
    void before() throws Exception {
        initMocks(this);

        when(queue.createChannel())
                .thenReturn(queueChannel);
        queueService = new QueueService(queue, objectMapper);
    }

    @Nested
    class receiveMessage {
        @Test
        void declares_queue_before_popping() throws Exception {
            queueService.receiveMessage(CLIENT_JID_1);

            InOrder inOrder = inOrder(queueChannel);
            inOrder.verify(queueChannel).declareQueue(CLIENT_JID_1);
            inOrder.verify(queueChannel).popMessage(CLIENT_JID_1);
        }

        @Test
        void receives_no_message() throws Exception {
            when(queueChannel.popMessage(CLIENT_JID_1))
                    .thenReturn(Optional.empty());
            assertThat(queueService.receiveMessage(CLIENT_JID_1)).isEmpty();
        }

        @Test
        void receives_one_message() throws Exception {
            when(queueChannel.popMessage(CLIENT_JID_1))
                    .thenReturn(Optional.of(QueueMessage.of(123, "the-message")));
            when(objectMapper.readValue("the-message", QueueService.ClientMessage.class))
                    .thenReturn(new QueueService.ClientMessage.Builder()
                            .sourceJid(CLIENT_JID_2)
                            .type("the-type")
                            .content("the-content")
                            .build());
            assertThat(queueService.receiveMessage(CLIENT_JID_1)).contains(new Message.Builder()
                    .id(123)
                    .sourceJid(CLIENT_JID_2)
                    .type("the-type")
                    .content("the-content")
                    .build());
        }

        @Test
        void receives_no_message_for_bogus_format() throws Exception {
            when(queueChannel.popMessage(CLIENT_JID_1))
                    .thenReturn(Optional.of(QueueMessage.of(123, "the-message")));
            when(objectMapper.readValue("the-message", QueueService.ClientMessage.class))
                    .thenThrow(JsonProcessingException.class);
            assertThat(queueService.receiveMessage(CLIENT_JID_1)).isEmpty();
        }
    }
}
