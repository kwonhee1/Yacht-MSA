# MessageQue

# 목차
1. 만든 이유 : Domain 간 메시지 통신을 위한 Message Queue Template
2. 가능한 통신 방식
3. 주요 함수 / 사용 예시
4. class diagram

## 만든 이유 : Message Queue를 사용하기 위한 Template
1. MSA 환경에서 도메인 간의 결합도를 낮추고 비동기 이벤트를 처리하기 위함
2. 메시지 발행(Publish)과 구독(Subscribe) 로직을 추상화하여 일관된 인터페이스 제공
3. Spring Data Redis를 기반으로 하며, 필요시 Log 기반의 가짜(Mock) 메시지 큐 제공

## 가능한 통신 방식
**1. Redis (used Lettuce)**   
Spring Data Redis의 `LettuceConnectionFactory` 및 `RedisTemplate` 사용  
**Redis Streams** 기능을 활용하여 메시지의 신뢰성 있는 전달(Acknowledge) 보장  
**2. Log (Mocking)**  
실제 메시지를 전송하지 않고 로그로만 출력 (테스트용)

## 주요 함수 / 사용 예시
### Generate MessageQue
먼저 `ConnectionFactory`를 초기화한 후 `MessageQue`를 생성합니다.

```java
// MessageQue(ServerDomain, ConnectionFactory);
MessageQue messageQue = new MessageQue(Domain.PART, ConnectionFactory.redisConnectionFactory(host, port, username, password));

Map subscribeBehaviour = Behaviour.builder()
        // .add(Topic SubscribeTopic, Behaviour<BasedEntity> subscribeBehaviour);
        .add(Topic.YACHT_DELETE, (DeletedEvent event)->System.out.println("delete parts by yacht id"))
        .build();

// messageQue.startSubscribe(Map<Topic, Behaviour<BasedEntity>>
messageQue.startSubscribe(subscribeBehaviour);
```

### 1. Message Publish (메시지 발행)
**Usage example**
```java
// 특정 토픽에 대한 Publisher 생성
MessagePublisher<CreateEvent> userCreatePublisher = messageQue.generatePublisher(Topic.USER_CREATE);

// 이벤트 발행 (내부적으로 Redis Stream에 ADD)
userCreatePublisher.publish(new CreateEvent(userId, token));
```

### 2. Message Subscribe (메시지 구독)
`Behaviour` 빌더를 사용하여 토픽별 구독 행위를 정의하고 구독을 시작합니다. 내부적으로 Consumer Group을 생성하여 메시지를 소비합니다.

**Usage example**
```java
Map<Topic, Behaviour<? extends BasedEvent>> behaviours = Behaviour.builder()
    .add(Topic.USER_CREATE, (CreateEvent event) -> {
        // 유저 생성 이벤트 처리 로직
        System.out.println("Received token: " + event.getToken());
    })
    .add(Topic.USER_DELETE, (DeletedEvent event) -> {
        // 유저 삭제 이벤트 처리 로직
    })
    .build();

// 구독 시작 (Consumer Group 기반 Stream 리스닝)
messageQue.startSubscribe(behaviours);
```
