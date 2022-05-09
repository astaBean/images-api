package com.gallery.interceptor;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomResponseFromController implements ResponseBodyAdvice<Object> {

	private final HttpSession session;

	public CustomResponseFromController(HttpSession session) {this.session = session;}

	@Override
	public boolean supports(MethodParameter returnType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body,
			MethodParameter returnType,
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request,
			ServerHttpResponse response) {

		List<Object> listOfReturnObjects = new ArrayList<>();
		listOfReturnObjects.add(body == null ? "" : body);
		listOfReturnObjects.add(session.getAttribute("siteNotificationMessages"));
		return listOfReturnObjects;
	}

}

