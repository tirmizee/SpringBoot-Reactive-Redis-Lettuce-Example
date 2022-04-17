package com.tirmizee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class SpringBootReactiveDataRedisExampleApplicationTests {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void connectionFactoryShouldNotNull() {
		ReactiveRedisConnectionFactory redisConnectionFactory = applicationContext.getBean(ReactiveRedisConnectionFactory.class);

		Assertions.assertThat(redisConnectionFactory).isNotNull();
		Assertions.assertThat(redisConnectionFactory).isOfAnyClassIn(LettuceConnectionFactory.class);
	}

	@Test
	void redisOperationShouldNotNull() {
		ReactiveRedisOperations redisOperations = applicationContext.getBean("redisOperations", ReactiveRedisOperations.class);
		Assertions.assertThat(redisOperations).isNotNull();
	}

	@Test
	void valueShouldEqual() {
		ReactiveRedisOperations redisOperations = applicationContext.getBean("redisOperations", ReactiveRedisOperations.class);

		String expected = "hello";

		Mono<Object> setResult = redisOperations.opsForValue().set("test", expected);
		Mono<Object> getResult = redisOperations.opsForValue().get("test");

		StepVerifier.create(setResult)
				.expectNext(true)
				.verifyComplete();

		StepVerifier.create(getResult)
				.expectNext(expected)
				.verifyComplete();
	}

}
