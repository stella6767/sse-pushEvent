package com.example.sse.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sse.config.MySseEmitter;
import com.example.sse.web.SSEController;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@Service
public class SinkService {

	
	private static final Logger log = LoggerFactory.getLogger(SinkService.class);

	
	private final SSEController sseController;
	private final MySseEmitter mySseEmitter; 

	
	
	@GetMapping("/test")
	public String 이게됨() throws IOException {
	
		//sseController.well("되면 놀랄 일이군.");
		//sseController.streamDateTime();	
		
		mySseEmitter.sseEmitter.send("haha");
		
		return "ok";
	}
	
	
	
	
}
