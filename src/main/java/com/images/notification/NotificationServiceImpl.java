package com.images.notification;

import com.images.tools.NotificationMessage;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@SessionAttributes("siteNotificationMessages")
public class NotificationServiceImpl implements NotificationService {

    private final List<NotificationMessage> messages = new ArrayList<>();


    @Override
    public void addInfoMessage(String msg) {
        addNotificationMessage(NotificationMessageType.INFO, msg);
    }

    @Override
    public void addErrorMessage(String msg) {
        addNotificationMessage(NotificationMessageType.ERROR, msg);
    }

    public Collection<NotificationMessage> getMessages() {
        return messages;
    }

    private void addNotificationMessage(NotificationMessageType type, String msg) {
        messages.add(new NotificationMessage(type, msg));
    }

}

