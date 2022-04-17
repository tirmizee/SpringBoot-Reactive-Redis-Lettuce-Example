# SpringBoot-Reactive-Data-Redis-Example

### dependencies

    implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'
	implementation 'io.lettuce:lettuce-core'
	implementation 'org.apache.commons:commons-pool2:2.11.1'


### docker-compose.yaml (running redis)

```yaml

version: '3.0'
services:
  redis:
    image: bitnami/redis:6.2.6
    container_name: redis-single
    ports:
      - '6379:6379'
    environment:
      - REDIS_PASSWORD=password123
#      - REDIS_PORT_NUMBER=7000       # default 6379


```

### application.yaml

```yaml

spring:
  redis:
    host: 0.0.0.0
    port: 6379
    password: password123
    lettuce:
      pool:
        enabled: true
        max-idle: 2
        max-active: 4
        min-idle: 1


```

### configuration

```java

@Configuration
public class RedisConfig {

    @Bean(value = "redisOperations")
    public ReactiveRedisOperations<String, Object> redisOperations(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext.newSerializationContext();
        RedisSerializationContext<String, Object> context = builder
                .key(keySerializer)
                .hashKey(keySerializer)
                .value(valueSerializer)
                .hashValue(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean(value = "userOperations")
    public ReactiveRedisOperations<String, User> userOperations(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<User> valueSerializer = new Jackson2JsonRedisSerializer<>(User.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, User> builder = RedisSerializationContext.newSerializationContext();
        RedisSerializationContext<String, User> context = builder
                .key(keySerializer)
                .hashKey(keySerializer)
                .value(valueSerializer)
                .hashValue(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean(value = "stringOperations")
    public ReactiveRedisOperations<String, String> stringOperations(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer serializer = new StringRedisSerializer();
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext(serializer);
        RedisSerializationContext<String, String> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}


```

### testing 

```java

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

```