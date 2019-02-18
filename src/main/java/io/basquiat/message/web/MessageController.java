package io.basquiat.message.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.basquiat.message.service.MessageService;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * created by basquiat
 * 
 * Message Controller
 *
 */
@Slf4j
@RestController
public class MessageController {

	@Autowired
	private MessageService messageService;

	@GetMapping("/message/type/{type}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void message(@PathVariable("type") String type) {
		log.info("Call message.....");
		messageService.sendMessage(type);
    }
	
}
