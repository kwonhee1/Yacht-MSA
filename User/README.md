# User
Yacht Project에서 User관련 기능을 맡고 있는 User Domain

## dependencies
| jdk | SpringBoot | JPA | mysql-connector | jwt    | PasswordEncoder | Common |
| --- |------------|-----|-----------------|--------|-----------------|--------|  
| 21  | 3.5.7      | BOM | mysql 8.0       | 0.11.5 | jbcrypt:0.4     | libs   |

## 성능 test 결과


## api 명세서
### 1. register 회원가입
Request

```
POST /user/public/register
Content-Type: application/json
Authorization: 없음

{
    "email" : "email", // not null
    "name" : "name" , // not null
    "password" : "password", // not null
    "token" : "fcm - token" // can null, null값은 허용하지만 null인경우 알림 발송 불가
}
```
Response
```
200 OK
Content-Type: application/json

{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message  | 사유     |
|-------------|----------------|--------|
| 400         | 잘못된 입력 파라미터입니다 | -      |
| 409         | conflict       | 이메일 중복 |

### 2. login 로그인
Request

```
POST /user/public/login
Content-Type: application/json
Authorization: 없음

{
    "email" : "email", // not null
    "password" : "password" // not null
}
```
Response
```
200 OK
Content-Type: application/json

{
    "status": 200,
    "message": "success",
    "response": {
        "token": "jwt-token"
    }
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|--------|
| 400 | 잘못된 입력 파라미터입니다 | - |
| 404 | not found | 존재하지 않는 이메일 |
| 400 | bad request | 비밀번호 불일치 |

### 3. 회원 정보 조회
Request

```
GET /user/api
userId: {userId} // Header, not null
```
Response
```
200 OK
Content-Type: application/json

{
    "status": 200,
    "message": "success",
    "response": {
        "email": "email",
        "name": "name"
    }
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |
| 404 | not found | 존재하지 않는 사용자 |

### 4. email 중복 확인
Request

```
GET /user/public/email-check?email={email}
Authorization: 없음
```
Response
```
200 OK
Content-Type: application/json

{
    "status": 200,
    "message": "exist", // or "not exist"
    "response": null
}
```

### 5. delete 회원 탈퇴
Request

```
DELETE /user/api
userId: {userId} // Header, not null
```
Response
```
200 OK
Content-Type: application/json

{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |
