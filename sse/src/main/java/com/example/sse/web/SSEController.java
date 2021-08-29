package com.example.sse.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.sse.config.MySseEmitter;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
public class SSEController {

	//https://turkogluc.com/server-sent-events-with-spring-boot-and-reactjs/
	private static final Logger LOGGER  = LoggerFactory.getLogger(SSEController.class);
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	private final MySseEmitter mySseEmitter; 

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }
    
    
    
    @GetMapping("/service")
    public String well(String testData) {
    	
    	LOGGER.info("이게 될까." + testData);
    	
    	return "이게 될까..";
    }
    
    
    
    
    

    @GetMapping("/time")
    @CrossOrigin
    public SseEmitter streamDateTime() {

//        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

    	mySseEmitter.sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

    	mySseEmitter.sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

    	mySseEmitter.sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

//        executor.execute(() -> {
//            for (int i = 0; i < 15; i++) {
//                try {
//                    //sseEmitter.send(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
//                    sseEmitter.send("");
//                    sleep(1, sseEmitter);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    sseEmitter.completeWithError(e);
//                }
//            }
//            sseEmitter.complete();
//        });

        LOGGER.info("Controller exits");
        return mySseEmitter.sseEmitter;
    }
    
    

    @GetMapping("/run")
    @CrossOrigin
    public SseEmitter doTheJob() {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

        sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

        ObservableProgress progress = new ObservableProgress(100);

        
        
        CompletableFuture.runAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(10));
        })
        .thenRunAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(20));
        })
        .thenRunAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(10));
        })
        .thenRunAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(20));
        })
        .thenRunAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(20));
        })
        .thenRunAsync(() -> {
            sleep(1, sseEmitter);
            pushProgress(sseEmitter, progress.increment(20));
        })
        .thenRunAsync(sseEmitter::complete)
        .exceptionally(ex -> {
            sseEmitter.completeWithError(ex);
            throw (CompletionException) ex;
        });

        LOGGER.info("Controller exits");
        return sseEmitter;
    }

    private void pushProgress(SseEmitter sseEmitter, ObservableProgress progress) {
        try {
            LOGGER.info("Pushing progress: {}", progress.toString());
            sseEmitter.send(SseEmitter.event().name("Progress").data(progress, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            LOGGER.error("An error occurred while emitting progress.", e);
        }
    }

    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sseEmitter.completeWithError(e);
        }
    }
    
    
    
    
//    public void consumeServerSentEvent() {
//        WebClient client = WebClient.create("http://localhost:8080/sse-server");
//        ParameterizedTypeReference<ServerSentEvent<String>> type
//         = new ParameterizedTypeReference<ServerSentEvent<String>>() {};
//
//        Flux<ServerSentEvent<String>> eventStream = client.get()
//          .uri("/stream-sse")
//          .retrieve()
//          .bodyToFlux(type);
//
//        eventStream.subscribe(
//          content -> logger.info("Time: {} - event: name[{}], id [{}], content[{}] ",
//            LocalTime.now(), content.event(), content.id(), content.data()),
//          error -> logger.error("Error receiving SSE: {}", error),
//          () -> logger.info("Completed!!!"));
//    }
}
