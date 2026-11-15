package com.radiantcat1.course2ics.common;

public enum ErrorCode {

    PARAM_INVALID(40001, "参数校验失败"),
    PERIOD_INVALID(40003, "节次不合法"),
    INTERNAL_ERROR(50001, "服务内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}