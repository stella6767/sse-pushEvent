package com.example.sse.web;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;
import reactor.core.publisher.Sinks.Many;

//@RequiredArgsConstructor
@RestController
public class FluxController {

	Sinks.Many<String> sink;
	AtomicLong counter;
	
	public FluxController() {	
		this.sink = Sinks.many().multicast().onBackpressureBuffer();  // unicast, multicast, replay
		this.counter = new AtomicLong();
	}
	

	
	@GetMapping("/flux")
	public Flux<Integer> returnFlux(){
		return Flux.just(1,2,3,4) 
				.delayElements(Duration.ofSeconds(1))
				.log();
	}
	
	@GetMapping(value="/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Long> returnFluxStream(){
		return Flux.interval(Duration.ofSeconds(1)) 
				.log();
	}
	
	
	// ~~~~~~~~~~~~~~~~~~~  SSE 프로토콜
	// 참고 : https://lts0606.tistory.com/306
	// 참고 : https://stackoverflow.com/questions/51370463/spring-webflux-flux-how-to-publish-dynamically
	
    @GetMapping("/send")
    public void test() {
        EmitResult result = sink.tryEmitNext("Hello World #" + counter.getAndIncrement()); // publishing (발행인)

        if (result.isFailure()) {
          // do something here, since emission failed
        }
    }

    @GetMapping(value="/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse() {
        return sink.asFlux().map(e -> ServerSentEvent.builder(e).build()); // processor (반응형 구독자)
    }


	

	
}
