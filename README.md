# cloud-stream-kafka
spring boot, spring-cloud-stream, kafka


# Redis vs. Kafka

See [Redis, Kafka, RabbitMQ](https://donchev.is/post/redis-kafka-ra)    


# 고전적인 레디스, 카프카 Configuration    
    

```
	 * redis connection factory
	 * @return JedisConnectionFactory
	 */
	@Bean
    public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(redisHost);
		jedisConnectionFactory.setPort(redisPort);
		jedisConnectionFactory.setPassword(redisPassword);
		jedisConnectionFactory.setUsePool(true);		
		return jedisConnectionFactory;
    }

	/**
	 * Redis Template Setup
	 * @return RedisTemplate<String, Object>
	 */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    /**
     * redis listner
     * @return RedisMessageListenerContainer
     */
    @Bean
    public RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        // subscribe 채널 등록
        container.addMessageListener(messageListener(), subTopics());
        return container;
    }

    /**
     * subscribe 채널 등록
     * @return ChannelTopic
     */
    @Bean
    public List<ChannelTopic> subTopics() {
    	List<ChannelTopic> list = new ArrayList<>();
    	list.add(new ChannelTopic(subChannel));
        return list;
    }
    
    /**
     * publish 채널 등록
     * @return ChannelTopic
     */
    @Bean(name="pub")
    public ChannelTopic pubTopic() {
    	return new ChannelTopic(pubChannel);
    }
    
    /**
     * retry 채널 등록
     * @return ChannelTopic
     */
    @Bean(name="retry")
    public ChannelTopic retryTopic() {
    	return new ChannelTopic(retryChannel);
    }
    
    /**
     * redis pub setup
     * @return MessageListenerAdapter
     */   
    @Bean
    public MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(redisTemplate(), pubTopic(), retryTopic());
    }

    /**
     * redis sub setup
     * @return MessageListenerAdapter
     */
    @Bean
    public MessageListenerAdapter messageListener() {
    	return new MessageListenerAdapter(new RedisMessageSubscriber(subChannel, walletService));
    }
```
    
        
이것을 단순하게 어노테이션과 application.yml 설정만으로 할 수 있게 도와주는 spring-cloud-starter-stream-kafka     

위와 같이 하지 말고 그냥 편하게 pub/sub을 구현하고 싶다!

````
public interface MessageStreams {
	
	String INPUT = "message-in";
	String OUT = "message-out";
	
	@Input(MessageStreams.INPUT)
	SubscribableChannel inboundMessage();
	
	@Output(MessageStreams.OUT)
	MessageChannel outboundMessage();
	
}
````

그냥 채널과 관련된 코드는 interface로 정의한다.
in/out은 어노테이션으로
     
         
         
````
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
````
서비스 구현은 그냥 위와 같이 채널 열고 보낼 메세지를 payload에 담아서 보낸다. 이때 header부분에 content_type과 형식을 지정한다.    
    
    
    
````	         
@Slf4j
@Component
public class MessageListner {

	@StreamListener(MessageStreams.OUT)
	public void handleMessage(@Payload Message message) {
		log.info("Received new message: {}", message);
	}
	
}
````

subscribe는 어디서?    
그냥 어노테이션으로 끝내버리자.
     
````
spring:
  profiles: local
  cloud:
    stream:
      kafka:
        binder:
          brokers: localhost:9092
      bindings:
        inboundMessage:
          destination: message
          contentType: application/json
        outboundMessage:
          destination: message
          contentType: application/json
````

application.yml은 다음과 같이 설정으로 끝낸다.    
MessageStreams에서 설정한 in/out 메쏘드를 message라는 topic으로 묶는다.    
    
    
# 왜 썼니?

어떤 요청이 들어오면 해당 서비스는 들어온 요청에 대한 특정 비지니스 로직을 탄다.
그 이후에 해당 task에 대한 처리를 id와 type으로 정보를 담아 redis 또는 kafka를 통해 다른 서비스로 요청 메세지를 보내는 형식이다.    

이 프로토타입은 서버 혼자서 주고 받는 식으로 테스트했기 때문에  @StreamListener사용시 내가 보낸 OUT채널로 리스너를 설정했지만 실제로 kafka를 통해 메세지를 주고 받을 때는 이 설정은 변경되어야 한다.