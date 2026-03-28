# MessageQue

# 목차
1. 만든 이유 : Domain 간 메시지 통신을 위한 Message Queue Template
2. 의존성 (Dependencies)
3. 주요 함수 / 사용 예시

## 만든 이유 : Message Queue를 사용하기 위한 Template
1. MSA 환경에서 도메인 간의 결합도를 낮추고 비동기 이벤트를 처리하기 위함
2. 메시지 발행(Publish)과 구독(Subscribe) 로직을 추상화하여 일관된 인터페이스 제공
3. Spring Data Redis를 기반으로 하며, 필요시 Log 기반의 가짜(Mock) 메시지 큐 제공

## 의존성 (Dependencies)
- **Spring Boot Version**: `3.5.7`
- **Dependency Management Plugin Version**: `1.1.7`
- **Language**: Java 21
- **Library**: 
  - `org.springframework.boot:spring-boot-starter-data-redis`
  - `org.springframework.boot:spring-boot-starter-web`
  - `com.fasterxml.jackson.core:jackson-databind`
  - `com.fasterxml.jackson.datatype:jackson-datatype-jsr310`

## 주요 함수 / 사용 예시
### Generate MessageQue
먼저 `ConnectionFactory`를 초기화한 후 `MessageQue`를 생성합니다.

```java
// MessageQue(ServerDomain, ConnectionFactory);
MessageQue messageQue = new MessageQue(Domain.PART, ConnectionFactory.redisConnectionFactory(host, port, username, password));

Map<Topic, SubscribeBehaviour<? extends BasedEvent>> subscribeBehaviour = SubscribeBehaviour.builder()
        // .add(Topic SubscribeTopic, Class receiveEventClass, Behaviour<E> subscribeBehaviour);
        .add(Topic.USER_DELETE, DeletedEvent.class, (DeletedEvent event)->System.out.println("delete parts by yacht id"))
        .build();

// messageQue.startSubscribe(Map<Topic, SubscribeBehaviour<? extends BasedEvent>>)
messageQue.startSubscribe(subscribeBehaviour);
```

### 1. Message Publish (메시지 발행)
**Usage example**
```java
// 특정 토픽에 대한 Publisher 생성
MessagePublisher<DeleteEvent> userCreatePublisher = messageQue.generatePublisher(Topic.USER_CREATE);

// 이벤트 발행 (내부적으로 Redis Stream에 ADD)
userCreatePublisher.publish(new DeleteEvent(userId, data));
```

### 2. Message Subscribe (메시지 구독)
`SubscribeBehaviour` 빌더를 사용하여 토픽별 구독 행위를 정의하고 구독을 시작합니다. 내부적으로 Consumer Group을 생성하여 메시지를 소비합니다.

**Usage example**
```java
Map<Topic, SubscribeBehaviour<? extends BasedEvent>> behaviours = SubscribeBehaviour.builder()
    // .add(Topic, ReceiveEventClass, Behaviour)
    .add(Topic.USER_DELETE, DeletedEvent.class,
        // 유저 삭제 이후 로직
        (DeletedEvent event) -> yachtService.deleteByUserId(event.getUserId())
    )
    .build();

// 구독 시작 (Consumer Group 기반 Stream 리스닝)
messageQue.startSubscribe(behaviours);
```