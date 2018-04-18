package io.suricate.monitoring.model.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import lombok.*;

import java.util.Date;

/**
 * Used for send errors through webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class ApiErrorDto extends AbstractDto {
    /**
     * The error message to send
     */
    private String message;
    /**
     * The key code
     */
    private String key;
    /**
     * The HttpStatus number
     */
    private int status;
    /**
     * The datetime of the error
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;


    public ApiErrorDto(ApiErrorEnum apiErrorEnum) {
        this.message = apiErrorEnum.getMessage();
        this.key = apiErrorEnum.getKey();
        this.timestamp = new Date();
        this.status = apiErrorEnum.getStatus().value();
    }
}
