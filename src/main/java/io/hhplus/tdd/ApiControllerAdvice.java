package io.hhplus.tdd;

import io.hhplus.tdd.exception.ExceededMaxPointException;
import io.hhplus.tdd.exception.ExceededPerPointException;
import io.hhplus.tdd.exception.InsufficientChargedPointException;
import io.hhplus.tdd.exception.InvalidPointException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ExceededMaxPointException.class)
    public ResponseEntity<ErrorResponse> handleExceededMaxPointException(ExceededMaxPointException e){
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = ExceededPerPointException.class)
    public ResponseEntity<ErrorResponse> handleExceededPerPointException(ExceededPerPointException e){
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = InsufficientChargedPointException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientChargedPointException(InsufficientChargedPointException e){
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = InvalidPointException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPointException(InvalidPointException e){
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }


}
