package io.basquiat.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 
 * created by basquiat
 * 어노테이션을 통해서 input/output채널을 설정할 수 있다.
 * 이것은 여러개의 채널을 간단하게 설정할 수 있도록 도와준다.
 * 기본적인 스프링 부트에서 redis, kafka를 설정할 때 요구하는 번잡한 설정이 필요없다.
 * 메세지에 대한 형식은 application.yml에서 설정한다.
 * @See application.yml
 * 
 */
public interface MessageStreams {
	
	String INPUT = "message-in";
	String OUT = "message-out";
	
	@Input(MessageStreams.INPUT)
	SubscribableChannel inboundMessage();
	
	@Output(MessageStreams.OUT)
	MessageChannel outboundMessage();
	
}
