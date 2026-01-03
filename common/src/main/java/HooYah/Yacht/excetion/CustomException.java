package HooYah.Yacht.excetion;

public class CustomException extends RuntimeException {

    public final int statusCode;
    // public final String message; // 상외 RuntimeException.class 의 message 변수 사용!

    public CustomException(ErrorCode errorCode, String... args) {
        super(String.format(errorCode.message, args));
        this.statusCode = errorCode.statusCode;
    }

}
