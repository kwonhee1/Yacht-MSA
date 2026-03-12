# Yacht Common Library

A shared library for common use across all microservices

## Stack

- **Language:** Java 21
- **Dependencies:** Jackson

## Key Features

### 1. WebClient(java.net.http.HttpClient)
- Implemented using java.net.http.HttpClient
- Include dependency : ObjectMapper

### 2. SuccessResponse
standard Response Object Class, using throughout the all project

### 3. ErrorCode
Provide Common ErrorCode, CustomException using throughout the all project

### 4. ListUtil
- `sortByRequestOrder` : sort List order by Request Order

## How to use

### ErrorCode
```java
// User MicroService :: UserService
if(isLoginFail) {
    throw new CustomException(ErrorCode.NOT_FOUND);
}
```

### WebClient

```java
// init WebClient (init once when starting project)
WebClient webClient = new WebClient(TimeZone.SEOUL, 5);

// ask to Answer
class Asker {
    WebResponse response = webClient.webClient("http://api-service/answer", WebClient.HttpMethod.GET, null);
    List<MyData> dataList = response.toList(MyData.class);
}

// Answer to Asker
class Answer {
    public SuccessResponse getUserInfoList(List<Long> userIdList) {
        // find User
        List<User> selectedUserList = userRepository.getUserList(userIdList);
        
        // sort User order by Request Order
        List<User> sortedUserList = ListUtil.sortByRequestOrder(userIdList, selectedUserList, (user)->user.getId());
        
        // Response
        return new SuccessResponse(200, "Success", data);
    }
}
```