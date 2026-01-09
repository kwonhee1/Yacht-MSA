package HooYah.Yacht.excetion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        e.printStackTrace();
        return ResponseEntity.status(e.statusCode).body(e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return ResponseEntity.status(ErrorCode.UN_AUTHORIZATION.statusCode).body(ErrorCode.UN_AUTHORIZATION.message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST_PARAMETER.statusCode).body(ErrorCode.INVALID_REQUEST_PARAMETER.message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
    }
}
