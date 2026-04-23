package com.devnguyen.test_skill.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
public enum ErrorCode {
    USER_EXISTED(1002, " USERNAME ĐÃ TỒN TẠI !! ",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003," Username >= 3 kí tự bro !! ",HttpStatus.BAD_REQUEST ),
    PASSWORD_INVALID(1004," Password >= 5 kí tự bro !! ", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"Người dùng KHÔNG tồn tại !!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Bro xác minh bằng token đi !!",HttpStatus.UNAUTHORIZED),
    UN_AUTHORIZED(1007,"Bro không có quyền !!",HttpStatus.FORBIDDEN),
    DOB_INVALID(1007,"Ngày sinh không hợp lệ !!",HttpStatus.BAD_REQUEST)
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;


    // constructor
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }


}
