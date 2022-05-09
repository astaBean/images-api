package com.gallery.services;

import com.gallery.tools.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@SessionAttributes("siteNotificationMessages")
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFY_MSG_SESSION_KEY = "siteNotificationMessages";
    private final HttpSession httpSession;

    private final List<String> messages = new ArrayList<>();

    @Autowired
    public NotificationServiceImpl(HttpSession httpSession) {this.httpSession = httpSession;}

    @Override
    public void addInfoMessage(String msg) {
        addNotificationMessage(NotificationMessageType.INFO, msg);
    }

    @Override
    public void addErrorMessages(List<ObjectError> errors) {
        errors.forEach(s -> addNotificationMessage(NotificationMessageType.ERROR, s.toString()));
    }

    @Override
    public void addErrorMessage(String msg) {
        addNotificationMessage(NotificationMessageType.ERROR, msg);
    }

    @Override
    public String getNotificationSessionKey() {
        return NOTIFY_MSG_SESSION_KEY;
    }

    private void addNotificationMessage(NotificationMessageType type, String msg) {
        messages.add(new NotificationMessage(type, msg).getText());
        httpSession.setAttribute(NOTIFY_MSG_SESSION_KEY, messages);
    }

    public enum NotificationMessageType {
        INFO, ERROR
    }

}

