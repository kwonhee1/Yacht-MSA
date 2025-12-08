package HooYah.Yacht.common.excetion;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 입력 파라미터입니다"),
    UN_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),

    NOT_FOUND(HttpStatus.NOT_FOUND, "not found"),
    CONFLICT(HttpStatus.CONFLICT, "conflict"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "bad request"),
    ;

    public HttpStatus status;
    public String message;

    ErrorCode(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

}
