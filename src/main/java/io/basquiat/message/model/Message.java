package io.basquiat.message.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Message {

	/**
	 * message id
	 */
	private String id;
	
	/**
	 * message type
	 */
	private String type;
	
}
