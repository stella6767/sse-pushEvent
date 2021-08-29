package com.example.sse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Configuration
public class MySseEmitter {

	public SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
	
}
