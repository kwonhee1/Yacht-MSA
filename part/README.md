# Part & Repair
Yacht Project에서 부품(Part) 및 수리(Repair) 내역 관리를 맡고 있는 Part Domain

## dependencies
| jdk | SpringBoot | JPA | mysql-connector | Redis | Common |
| --- |------------|-----|-----------------|-------|--------|
| 21  | 3.5.7      | BOM | mysql 8.0       | libs  | libs   |

## 성능 test 결과


## api 명세서
### 1. 부품 리스트 조회
Request
```
GET /part/api/{yachtId}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "partList": [
            {
                "id": 1,
                "name": "부품 이름",
                "manufacturer": "제조사",
                "model": "모델명",
                "interval": 24,
                
                "lastRepair": "2024-05-20T00:00:00Z"
            }
        ]
    }
}
```
Error

| http status | error message | 사유                    |
|-------------|----------------|-----------------------|
| 401 | un authorization | 로그인 필요 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 2. 부품 추가
Request
```
POST /part/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "yachtId": 1,
    "name": "부품 이름",
    "manufacturer": "제조사",
    "model": "모델명",
    "interval": 24,
    
    "lastRepair": "2025-11-18T00:14:56+09:00" // can null
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
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |

### 3. 부품 수정
Request
```
PUT /part/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "id": 1, // part id, not null
    "name": "수정된 부품 이름",
    "manufacturer": "수정된 제조사",
    "model": "수정된 모델명",
    "interval": 12
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
| 404 | not found | 존재하지 않는 부품 |
| 409 | conflict | 요트 접근 권한이 없음 |

### 4. 부품 삭제
Request
```
DELETE /part/api/{partId}
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

| http status | error message | 사유                    |
|-------------|----------------|-----------------------|
| 401 | un authorization | 로그인 필요                |
| 404 | not found | 존재하지 않는 부품            |
| 409 | conflict | 요트 접근 권한이 없음 |

### 5. 수리 내역 리스트 조회
Request
```
GET /repair/api/{partId}
userId: {userId} // Header, not null
```
Response
```
200 OK
{
    "status": 200,
    "message": "success",
    "response": {
        "repairList": [
            {
                "id": 1,
                "repairDate": "2025-11-14T09:00:00+09:00",
                "content": "수리 내용", // can empty (빈 문자열일 수 있습니다)
                "user": { // 수리한 사람
                    "name": "이름",
                    "email": "이메일"
                }
            }
        ]
    }
}
```
Error

| http status | error message | 사유                    |
|-------------|----------------|-----------------------|
| 401 | un authorization | 로그인 필요                |
| 404 | not found | 존재하지 않는 부품            |
| 409 | conflict | 요트 접근 권한이 없음 |

### 6. 수리 내역 추가
Request
```
POST /repair/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "id": 1, // partId
    "content": "수리 내용",
    "date": "2024-01-20T00:00:00Z"
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
| 404 | not found | 존재하지 않는 부품            |
| 409 | conflict | 요트 접근 권한이 없음 |

### 7. 수리 내역 수정
Request
```
PUT /repair/api
Content-Type: application/json
userId: {userId} // Header, not null

{
    "id": 1, // repairId
    "content": "수정된 수리 내용",
    "date": "2024-01-21T00:00:00Z"
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
| 401 | un authorization | 로그인 필요        |
| 404 | not found | 존재하지 않는 수리 내역 |
| 409 | conflict | 요트 접근 권한이 없음  |

### 8. 수리 내역 삭제
Request
```
DELETE /repair/api/{repairId}
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
| 404 | not found | 존재하지 않는 수리 내역 |
| 409 | conflict | 없는 요트이거나 요트 접근 권한이 없음 |
