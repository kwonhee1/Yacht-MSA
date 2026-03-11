# Yacht
Yacht Project에서 Yacht(요트) 및 Yacht-User(요트 멤버) 관련 기능을 맡고 있는 Yacht Domain

## dependencies
| jdk | SpringBoot | JPA | mysql-connector | Redis | Common |
| --- |------------|-----|-----------------|-------|--------|
| 21  | 3.5.7      | BOM | mysql 8.0       | libs  | libs   |

## 성능 test 결과


## api 명세서
### 1. Yacht 생성
Request
```
POST /yacht/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "yacht": {
        "name": "yacht name", // not null
        "nickName": "yacht nickName"
    },
    "partList": [ // optional, 초기 부품 리스트
        {
            "name": "part name",
            "period": 100
        }
    ]
}
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|----|
| 400 | 잘못된 입력 파라미터입니다 | -  |
| 401 | un authorization | 로그인 필요  |

### 2. Yacht 수정
Request
```
PUT /yacht/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "id": 1,
    "name": "new yacht name", // not null
    "nickName": "new yacht nickName"
}
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message | 사유                        |
|-------------|----------------|---------------------------|
| 400 | 잘못된 입력 파라미터입니다 | -                         |
| 401 | un authorization | 로그인 필요                         |
| 404 | not found | 존재하지 않는 요트이거나 관리 User가 아님 |

### 3. Yacht 삭제
Request
```
DELETE /yacht/api/{yachtId}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message | 사유                        |
|-------------|----------------|---------------------------|
| 401 | un authorization | 로그인 필요                         |
| 404 | not found | 존재하지 않는 요트이거나 관리 User가 아님 |

### 4. Yacht 리스트 조회
Request
```
GET /yacht/api
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "list": [
            {
                "id": 1,
                "name": "yacht name",
                "nickName": "yacht nickName"
            }
        ]
    }
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |

### 5. Yacht 멤버 리스트 조회
Request
```
GET /yacht/api/user/{yachtId}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "userList": [
            {
                "id": 1,
                "name": "user name",
                "email": "user email"
            }
        ]
    }
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |

### 6. Yacht 초대 코드 생성
Request
```
GET /yacht/api/invite?yachtId={yachtId}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "code": 123456789
    }
}
```
Error

| http status | error message | 사유 |
|-------------|----------------|----|
| 401 | un authorization | 로그인 필요  |

### 7. 초대 코드로 Yacht 참여
Request
```
POST /yacht/api/invite
Content-Type: application/json
userId: {userId} // Header, not null

{
    "code": 123456789
}
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": null
}
```
Error

| http status | error message | 사유            |
|-------------|----------------|---------------|
| 400 | 잘못된 입력 파라미터입니다 | -             |
| 401 | un authorization | 로그인 필요             |
| 404 | not found | 유효하지 않은 초대 코드 |
