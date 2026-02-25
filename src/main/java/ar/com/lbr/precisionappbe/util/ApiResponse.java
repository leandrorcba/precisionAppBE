package ar.com.lbr.precisionappbe.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Long total;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, Long total) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.total = total;
    }
}
