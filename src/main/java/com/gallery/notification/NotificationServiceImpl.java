package com.gallery.notification;

import com.gallery.tools.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@SessionAttributes("siteNotificationMessages")
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFY_MSG_SESSION_KEY = "siteNotificationMessages";
    private final List<NotificationMessage> messages = new ArrayList<>();

    private final HttpSession httpSession;

    @Autowired
    public NotificationServiceImpl(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

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
        httpSession.setAttribute(NOTIFY_MSG_SESSION_KEY, messages);
    }

}

