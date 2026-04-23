package com.devnguyen.test_skill.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
public enum ErrorCode {
    USER_EXISTED(1002, " USERNAME ĐÃ TỒN TẠI !! ",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003," Username >= 3 ki tu !! ",HttpStatus.BAD_REQUEST ),
    PASSWORD_INVALID(1004," Password >= 5 ki tu !! ", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User NOT Existed !!", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Un Authentication !!",HttpStatus.UNAUTHORIZED),
    UN_AUTHORIZED(1007,"you do not have permission !!",HttpStatus.FORBIDDEN)
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
