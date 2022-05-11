package com.gallery.notification;

import com.gallery.tools.NotificationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
class NotificationServiceImplUnitTest {

    private NotificationServiceImpl service;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        service = new NotificationServiceImpl(httpSession);
    }

    @Test
    void testAddInfoMessage_whenInfoMessageAdded_thenMessageShouldBePresentInMessageList() {
        final String message = "Potato is tasty";

        service.addInfoMessage(message);

        final NotificationMessage addedNotification = service.getMessages().iterator().next();
        assertThat(addedNotification.getType(), equalTo(NotificationMessageType.INFO));
        assertThat(addedNotification.getText(), equalTo(message));
    }

    @Test
    void testAddErrorMessage_whenErrorMessageAdded_thenMessageShouldBePresentInMessageList() {
        final String message = "Error message";

        service.addErrorMessage(message);

        final NotificationMessage addedNotification = service.getMessages().iterator().next();
        assertThat(addedNotification.getType(), equalTo(NotificationMessageType.ERROR));
        assertThat(addedNotification.getText(), equalTo(message));
    }


}
