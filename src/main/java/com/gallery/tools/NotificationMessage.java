package com.gallery.tools;

import com.gallery.notification.NotificationMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationMessage {

    private NotificationMessageType type;
    private String text;
}
