package HooYah.Yacht.excetion;

public enum ErrorCode {

    INVALID_REQUEST_PARAMETER(400, "잘못된 입력 파라미터입니다"),
    UN_AUTHORIZATION(401, "로그인이 필요합니다"),

    NOT_FOUND(404, "not found"),
    CONFLICT(409, "conflict"),
    BAD_REQUEST(400, "bad request"),

    API_FAIL(500, "proxy request fail : %s"),
    JACKSON_EXCEPTION(406, "jackson exception %s")
    ;

    public int statusCode;
    public String message;

    ErrorCode(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

}
