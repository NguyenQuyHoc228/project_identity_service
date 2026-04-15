package com.devnguyen.test_skill.exception;

public enum ErrorCode {
    USER_EXISTED(1002, " USERNAME ĐÃ TỒN TẠI !! "),
    USERNAME_INVALID(1003," Username >= 3 ki tu !! "),
    PASSWORD_INVALID(1004," Password >= 5 ki tu !! "),
    USER_NOT_EXISTED(1005,"User NOT Existed !!"),
    UNAUTHENTICATION(1006,"Un Authentication !!")
    ;

    private int code;
    private String message;


    // constructor
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    // get
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
