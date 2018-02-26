package io.suricate.monitoring.model.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.suricate.monitoring.model.enums.ApiErrorEnum;

import java.util.Date;

public class ApiErrorDto {
    private String message;
    private String key;
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;


    public ApiErrorDto(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.key = apiErrorEnum.getKey();
        this.timestamp = new Date();
        this.status = apiErrorEnum.getStatus().value();
    }


    public String getMessage() {
        return message;
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
