package com.kdy.live.sched.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	@Value("${spring.redis.port}")
	public int port;

	@Value("${spring.redis.host}")
	public String host;
	
	@Value("${spring.redis.password}")
	public String password;
	
	@Value("${spring.redis.sentinelYn}")
	public String sentinelYn;
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		
		if(sentinelYn.equals("Y")) {
			RedisSentinelConfiguration redisSentinelConfiguration = 
					new RedisSentinelConfiguration()
					.master("mymaster")
					.sentinel(host, port);
			redisSentinelConfiguration.setPassword(password);
			return new LettuceConnectionFactory(redisSentinelConfiguration);
		}

		if(password != null && !password.isEmpty()) {
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host,port);
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
			return new LettuceConnectionFactory(redisStandaloneConfiguration);
		}

		return new LettuceConnectionFactory(host, port);
	}
	
	@Bean
	public RedisMessageListenerContainer redisContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(this.redisConnectionFactory());
		return container;
	}	
	
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}
	
	@Bean
	public RedisTemplate<?, ?> redisTemplateObject() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		return redisTemplate;
	}
}
