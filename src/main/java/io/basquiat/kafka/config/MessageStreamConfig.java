package io.basquiat.kafka.config;

import org.springframework.cloud.stream.annotation.EnableBinding;

import io.basquiat.kafka.MessageStreams;

/**
 * 
 * created by basquiat
 * 
 * 구현체가 없는 interface인 MessageStreams를 autowired할수 있도록 바인딩한다.
 * 이 설정이 없다면 interface로 정의된 MessageStreams를 Autowired할 수 없다.
 *
 */
@EnableBinding(MessageStreams.class)
public class MessageStreamConfig {

}
