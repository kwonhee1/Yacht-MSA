# Calendar & Alarm
Yacht Project에서 일정(Calendar) 관리 및 푸시 알림(Alarm) 기능을 맡고 있는 Calendar Domain

## dependencies
| jdk | SpringBoot | JPA | mysql-connector | FCM            | Redis | Common |
| --- |------------|-----|-----------------|----------------|-------|--------|
| 21  | 3.5.7      | BOM | mysql 8.0       | firebase 9.2.0 | libs  | libs   |

## 성능 test 결과


## api 명세서
### 1. 일정 생성
Request
```
POST /calendar/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "title": "일정 제목",
    "content": "일정 내용",
    "date": "2024-05-20",
    "yachtId": 1
}
```
Response
```
201 Created
{
    "status": 201,
    "message": "success",
    "response": {
        "id": 1,
        "title": "일정 제목",
        "content": "일정 내용",
        "date": "2024-05-20"
    }
}
```
Error
| http status | error message | 사유 |
|-------------|----------------|--------|
| 400 | 잘못된 입력 파라미터입니다 | - |
| 401 | un authorization | 로그인 필요 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 2. 일정 단건 조회
Request
```
GET /calendar/api/{id}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "id": 1,
        "title": "일정 제목",
        "content": "일정 내용",
        "date": "2024-05-20"
    }
}
```
Error
| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |
| 404 | not found | 존재하지 않는 일정 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 3. 일정 리스트 조회
Request
```
GET /calendar/api
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": [
        {
            "id": 1,
            "title": "일정 제목",
            "content": "일정 내용",
            "date": "2024-05-20"
        }
    ]
}
```
Error
| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |

### 4. 일정 수정
Request
```
PUT /calendar/api/{id}
Content-Type: application/json
userId: {userId} // Header, not null

{
    "title": "수정된 제목",
    "content": "수정된 내용",
    "date": "2024-05-21"
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
|-------------|----------------|--------|
| 400 | 잘못된 입력 파라미터입니다 | - |
| 401 | un authorization | 로그인 필요 |
| 404 | not found | 존재하지 않는 일정 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 5. 일정 삭제
Request
```
DELETE /calendar/api/{id}
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
| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |
| 404 | not found | 존재하지 않는 일정 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 6. 알림 리스트 조회
Request
```
GET /alarm/api/
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": [
        {
            "id": 1,
            "title": "알림 제목",
            "content": "알림 내용",
            "date": "2024-05-20"
        }
    ]
}
```
Error
| http status | error message | 사유 |
|-------------|----------------|--------|
| 401 | un authorization | 로그인 필요 |
