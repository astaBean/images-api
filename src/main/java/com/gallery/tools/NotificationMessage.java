package com.gallery.tools;

import com.gallery.services.NotificationServiceImpl;

public class NotificationMessage {

	NotificationServiceImpl.NotificationMessageType type;
	String text;

	public NotificationMessage(NotificationServiceImpl.NotificationMessageType type, String text) {
		this.type = type;
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
