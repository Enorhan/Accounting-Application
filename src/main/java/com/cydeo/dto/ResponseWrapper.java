package com.cydeo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseWrapper {
    private boolean success;
    private String message;
    private Integer code;
    private Object data;
}