package com.images.tools;

import com.images.notification.NotificationMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationMessage {

    private NotificationMessageType type;
    private String text;
}
