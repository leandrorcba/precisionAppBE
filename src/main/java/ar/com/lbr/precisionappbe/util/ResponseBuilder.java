package ar.com.lbr.precisionappbe.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data, Long total) {
        return ResponseEntity.ok(new ApiResponse<>(true, null, data, total));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null, 0L), status);
    }
}
