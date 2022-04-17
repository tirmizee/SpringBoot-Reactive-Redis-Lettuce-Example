package com.tirmizee;

import com.tirmizee.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
	private ApplicationContext applicationContext;
	private ReactiveRedisConnectionFactory redisConnectionFactory;
	private ReactiveRedisOperations<String, Object> redisOperations;
	private ReactiveRedisOperations<String, String> stringOperations;
	private ReactiveRedisOperations<String, User> userOperations;

	@BeforeEach
	public void init() {
		redisConnectionFactory = applicationContext.getBean(ReactiveRedisConnectionFactory.class);
		redisOperations = applicationContext.getBean("redisOperations", ReactiveRedisOperations.class);
		stringOperations = applicationContext.getBean("stringOperations", ReactiveRedisOperations.class);
		userOperations = applicationContext.getBean("userOperations", ReactiveRedisOperations.class);
	}

	@Test
	void connectionFactoryShouldNotNull() {
		Assertions.assertThat(redisConnectionFactory).isNotNull();
		Assertions.assertThat(redisConnectionFactory).isOfAnyClassIn(LettuceConnectionFactory.class);
	}

	@Test
	void redisOperationShouldNotNull() {
		Assertions.assertThat(redisOperations).isNotNull();
	}

	@Test
	void stringOperationShouldNotNull() {
		Assertions.assertThat(stringOperations).isNotNull();
	}

	@Test
	void userOperationShouldNotNull() {
		Assertions.assertThat(userOperations).isNotNull();
	}

	@Test
	void redisOperationsValueShouldEqual() {

		String expected = "hello";

		Mono<Boolean> setResult = redisOperations.opsForValue().set("test", expected);
		Mono<Object> getResult = redisOperations.opsForValue().get("test");

		StepVerifier.create(setResult)
				.expectNext(true)
				.verifyComplete();

		StepVerifier.create(getResult)
				.expectNext(expected)
				.verifyComplete();
	}

	@Test
	void stringOperationsValueShouldEqual() {

		String key = "uid";
		String expected = "111122222";

		Mono<Boolean> setResult = stringOperations.opsForValue().set(key, expected);
		Mono<String> getResult = stringOperations.opsForValue().get(key);

		StepVerifier.create(setResult)
				.expectNext(true)
				.verifyComplete();

		StepVerifier.create(getResult)
				.expectNext(expected)
				.verifyComplete();
	}

	@Test
	void userOperationsValueShouldEqual() {

		String key = "usr";
		User expected = new User(1l, "tirmizee");

		Mono<Boolean> setResult = userOperations.opsForValue().set(key, expected);
		Mono<User> getResult = userOperations.opsForValue().get(key);

		StepVerifier.create(setResult)
				.expectNext(true)
				.verifyComplete();

		StepVerifier.create(getResult)
				.expectNext(expected)
				.verifyComplete();
	}

}
