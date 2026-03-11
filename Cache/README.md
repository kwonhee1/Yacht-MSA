# CacheService

# 목차
1. 만든 이유 : Cache를 사용하기 위한 Template
2. 가능한 Cache 방식
3. 주요 함수 / 사용 예시
4. class diagram

## 만든 이유 : Cache를 사용하기 위한 Template
1. 반복되는 Redis Cache 사용 함수를 모아서 관리하기 위함
2. Spring, SpringBoot가 아닌 Service에서 또한 Redis Cache를 똑같이 사용하기 위함
3. Redis가 문제가 생겼을 때 Local Cache를 통한 CacheService를 제공하기 위함

## 가능한 Cache 방식
**1. Redis (used Jedis)**   
Jedis, apache.commons-pool 사용  
**2. InMemory (used Local Memory)**  
ConcurrentHashMap 사용

## 주요 함수 / 사용 예시
### Generate CacheService
first init Cache **Pool** before init CacheService
```java
Pool cachePool;
try {
    // try generate RedisPool
    cachePool = Cache.generateRedisPool(host, port, username, password, maxConnectionCount); // throws ConnectFailExeption
} catch (ConnectFailException e) {
    // generate InMemoryPool
    cachePool = Cache.generateInMemoryPool();
}
```

and make CacheService with generated Pool
```java
CachService userCacheService = Cache.cacheService("user", cachePool);
```
### 1. getOrSelect
**Method signature**
```java
T getOrSelect(Long keyId, Select<T> selectMethod);
```
try get CachedData and return cachedValue  
if not exists execute selectMethod and save selectValue
```mermaid
sequenceDiagram
    participant Caller
    participant CacheService
    participant CacheData
    participant Selector as Select<T>

    Caller->>CacheService: getOrSelect(keyId, selectMethod)
    CacheService->>CacheData: get(keyId)
    CacheData-->>CacheService: cachedValue

    alt cachedValue != null
        CacheService-->>Caller: cachedValue
    else cachedValue == null
        CacheService->>Selector: execute()
        Selector-->>CacheService: selectValue
        CacheService->>CacheData: set(keyId, selectValue)
        CacheService-->>Caller: selectValue
    end
```
**Usage example**
```java
CacheService userCacheService; // generated CacheService

User userEntity = userCacheService.getOrSelect(
        userId, // key Id
        ()->userRepository.findById(userId) // executed only on cache miss
);
```

---

### 2. getListOrSelect (multi getOrSelect)

**Method signature**

```java
List<T> getListOrSelect(List<Long> keyIds, Select<List<T>> selectMethod);
```
when get many CachedData if calling 'getOrSelect' multiple times can cause an N+1
use getListOrSelect method instead

selectMethod must return a List that **contains all values for keyIds** and is **sorted in the same order as keyIds.**  
(selectMethod 함수의 반환값은 언제나 keyIds의 모든 값을 가져야 하면 keyIds오 동일한 순서로 정렬되어 있어야 합니다)

**Usage example**

```java
CacheService<User> userCacheService; // generated CacheService

List<User> users = userCacheService.getListOrSelect(
        userIds, // key id list
        () -> userRepository.findAllById(userIds) // must be sorted by userIds
);
```

---

### 3. getListOrSelect (multi getOrSelect with group key)

**Method signature**

```java
List<T> getListOrSelect(Long groupKeyId, List<Long> keyIds, Select<List<T>> selectMethod);
```

Only the groupKeyId parameter has been added from "getListOrSelect(List<Long> keyIds, Select<List<T>> selectMethod)"

selectMethod must return a List that **contains all values for keyIds** and is **sorted in the same order as keyIds.**  
(selectMethod 함수의 반환값은 언제나 keyIds의 모든 값을 가져야 하며 keyIds와 동일한 순서로 정렬되어 있어야 합니다)

**Usage example**

```java
CacheService<User> userCacheService; // generated CacheService

List<User> users = userCacheService.getListOrSelect(
        groupKeyId, // group cache key
        userIds,    // key id list
        () -> userRepository.findAllById(userIds) // must be sorted by userIds
);
```

### Class Overview

#### Pool class  
- connect to Cache DB (or create InMemory Store)   
- publish and manage Connections

#### Connection  
- execute cache commands
#### Template :: Cache를 사용하기 위한 기본 UserInterface
- provides basic cache logics

#### CacheService :: Template를 사용하여 구현한 Service 코드
- provides CacheValue as Object  
- Distinguish between null values and empty values  
- provides duplicated cache logics like getOrSelect


## class diagram
```mermaid
classDiagram
    class CacheService~T~ {
        -category String
        -template Template
        -objectMapper ObjectMapper
        +add(id Long, value T) void
        +getOrSelect(selectId Long, select Select~T~) T
        +getOrSelect(subjectId Long, selectId Long, select Select~T~) T
        +getListOrSelect(selectIdList List~Long~, select Select~List~T~~) List~T~
        +getListOrSelect(subjectId Long, selectIdList List~Long~, select Select~List~T~~) List~T~
    }
    class Template {
        -pool Pool
        +add(key String, value String, second SaveSecond) void
        +addAll(keyList List~String~, valueList List~String~, second SaveSecond) void
        +get(key String, second SaveSecond) String
        +getList(keyList List~String~, second SaveSecond) List~String~
    }
    class Pool {
        <<interface>>
        +getConnection() Connection
        +returnResource(connection Connection) void
        +close() void
    }
    class Connection {
        <<interface, AutoCloseable>>
        +set(key String, value String, second SaveSecond) Connection
        +get(key String, second SaveSecond) Connection
        +sync() List~String~
        +close() void
    }

class JedisPool {
-pool redis.JedisPool
+JedisPool(host, port, username, password, maxConnection)
+getConnection() Connection
+returnResource(connection Connection) void
+close() void
}

class InMemoryPool {
-publishedConnections List~Connection~
-inMemory Map~String, String~
-isClosed boolean
+getConnection() Connection
+returnResource(connection Connection) void
+close() void
}

class JedisConnection {
-jedis Jedis
-pipeline Pipeline
-resultList List~Response~String~~
+JedisConnection(jedis Jedis)
+set(key, value, second) Connection
+get(key, second) Connection
+sync() List~String~
+close() void
}

class InMemoryConnection {
-from Pool
-inMemory Map~String, String~
-add Map~String, String~
-result List~String~
-isClosed boolean
+InMemoryConnection(pool, inMemory)
+set(key, value, second) Connection
+get(key, second) Connection
+sync() List~String~
+close() void
}

    %% Layer flow
    CacheService ..> Template : uses
    Template ..> Pool : uses
    
    Pool ..> Connection : creates
    
    Pool <|-- JedisPool : implements
    Pool <|-- InMemoryPool : implements
    
    Connection <|-- JedisConnection : implements
    Connection <|-- InMemoryConnection : implements
```
