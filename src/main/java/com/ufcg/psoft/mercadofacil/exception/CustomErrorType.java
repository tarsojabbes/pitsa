package com.ufcg.psoft.mercadofacil.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrorType {
    private LocalDateTime timestamp;
    private String message;
    private List<String> errors;
    public CustomErrorType(MercadoFacilException e) {
        this.timestamp = LocalDateTime.now();
        this.message = e.getMessage();
        this.errors = new ArrayList<>();
    }
}
