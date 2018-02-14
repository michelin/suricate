package io.suricate.monitoring.model.dto.error;

import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.util.Date;

public class ApiErrorDto {
    private String message;
    private int code;
    private String key;
    private int status;
    private Date timestamp;


    public ApiErrorDto(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.code = apiErrorEnum.ordinal();
        this.key = apiErrorEnum.getKey();
        this.timestamp = new Date();
        this.status = apiErrorEnum.getStatus().value();
    }


    public String getMessage() {
        return message;
    }
    public int getCode() {
        return code;
    }
    public String getKey() {
        return key;
    }
    public int getStatus() {
        return status;
    }
    public Date getTimestamp() {
        return timestamp;
    }
}
