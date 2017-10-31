package judgels.sealtiel.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import judgels.client.ClientChecker;
import judgels.client.api.Client;
import judgels.client.api.auth.BasicAuthHeader;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.queue.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class MessageResourceTests {
    private static final Client CLIENT_1 = Client.of("JIDSECL-1", "secret-1");
    private static final Client CLIENT_2 = Client.of("JIDSECL-2", "secret-2");
    private static final BasicAuthHeader AUTH_HEADER_1 = BasicAuthHeader.of(CLIENT_1);

    @Mock private ClientChecker clientChecker;
    @Mock private QueueService queueService;
    private MessageResource resource;

    @BeforeEach void before() {
        initMocks(this);

        resource = new MessageResource(clientChecker, queueService);
    }

    @Nested class receiveMessage {
        @Test void receives_no_message() throws Exception {
            when(queueService.receiveMessage(CLIENT_1.getJid()))
                    .thenReturn(Optional.empty());
            assertThat(resource.receiveMessage(AUTH_HEADER_1)).isEmpty();
        }

        @Test void receives_one_message() throws Exception {
            Message message = mock(Message.class);
            when(queueService.receiveMessage(CLIENT_1.getJid()))
                    .thenReturn(Optional.of(message));
            assertThat(resource.receiveMessage(AUTH_HEADER_1)).contains(message);
        }
    }

    @Nested class confirmMessage {
        @Test void confirms() throws Exception {
            resource.confirmMessage(AUTH_HEADER_1, 123);
            verify(queueService).confirmMessage(123);
        }
    }

    @Nested class sendMessage {
        @Test void sends() throws Exception {
            MessageData messageData = new MessageData.Builder()
                    .targetJid(CLIENT_2.getJid())
                    .type("the-type")
                    .content("the-content")
                    .build();
            resource.sendMessage(AUTH_HEADER_1, messageData);
            verify(queueService).sendMessage(CLIENT_1.getJid(), CLIENT_2.getJid(), "the-type", "the-content");
        }
    }
}
