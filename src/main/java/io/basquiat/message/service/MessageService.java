package io.basquiat.message.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import io.basquiat.kafka.MessageStreams;
import io.basquiat.message.model.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * created by basquiat
 *
 */
@Slf4j
@Service("messageService")
public class MessageService {

	@Autowired
	private MessageStreams messageStreams;
	
	public void sendMessage(String type) {
		
		// 1. message channnel create
		MessageChannel messageChannel = messageStreams.outboundMessage();
		
		// 2. create message for payload
		Message payloadMessage = Message.builder().id(UUID.randomUUID().toString()).type(type).build();
		
		// 3. send
		messageChannel.send(MessageBuilder.withPayload(payloadMessage)
										  .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
										  .build());
		log.info("Sending Message {}", payloadMessage);
	}

}
