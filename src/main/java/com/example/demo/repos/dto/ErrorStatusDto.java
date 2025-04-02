package com.example.demo.repos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.server.ResponseStatusException;

@Data
@AllArgsConstructor
public class ErrorStatusDto {

    public int status;

    public String message;

    public static ErrorStatusDto fromStatusException(ResponseStatusException e) {
        return new ErrorStatusDto(e.getStatusCode().value(), e.getReason());
    }
}
