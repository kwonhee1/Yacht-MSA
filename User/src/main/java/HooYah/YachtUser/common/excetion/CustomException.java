package HooYah.YachtUser.common.excetion;

public class CustomException extends RuntimeException {

    public ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }
}
