package HooYah.YachtUser.common.excetion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        e.printStackTrace();
        return ResponseEntity.status(e.errorCode.status).body(e.errorCode.message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST_PARAMETER.status).body(ErrorCode.INVALID_REQUEST_PARAMETER.message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
    }
}
