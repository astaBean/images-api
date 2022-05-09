package com.gallery.services;

import org.springframework.validation.ObjectError;

import java.util.List;

public interface NotificationService {

	void addInfoMessage(String msg);

	void addErrorMessage(String msg);

	void addErrorMessages(List<ObjectError> errors);

	String getNotificationSessionKey();
}
