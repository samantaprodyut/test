package com.iimj.resultportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Key serializer
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // Value serializer
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        serializer.setObjectMapper(objectMapper);
//
//        template.setValueSerializer(serializer);
//        template.setHashValueSerializer(serializer);
//        template.afterPropertiesSet();
//
//        return template;
//    }
	

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
	    RedisTemplate<String, Object> template = new RedisTemplate<>();
	    template.setConnectionFactory(connectionFactory);

	    // Keys as strings
	    template.setKeySerializer(new StringRedisSerializer());

	    // Values as JSON with JavaTime support
	    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule()); // <-- important
	    mapper.findAndRegisterModules(); // optional, also registers JavaTimeModule
	    serializer.setObjectMapper(mapper);

	    template.setValueSerializer(serializer);
	    template.setHashValueSerializer(serializer);

	    template.setHashKeySerializer(new StringRedisSerializer());
	    template.afterPropertiesSet();
	    return template;
	}
}