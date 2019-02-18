package io.basquiat.kafka;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import io.basquiat.message.model.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * created by basquiat
 * input 채널 즉 Subscribe(구독)한 채널에 대한 리스너 작성만으로 끝낸다.
 * @Payload는 해당 브로커로 들어오는 정보를 매핑할 모델을 적용하면 내부적으로 바인딩 처리한다.
 *
 */
@Slf4j
@Component
public class MessageListner {

	@StreamListener(MessageStreams.OUT)
	public void handleMessage(@Payload Message message) {
		log.info("Received new message: {}", message);
	}
	
}
