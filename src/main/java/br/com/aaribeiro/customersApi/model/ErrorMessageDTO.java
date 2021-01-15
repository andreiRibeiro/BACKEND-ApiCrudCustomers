package br.com.aaribeiro.customersApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {
    private String type;
    private String currentDate;
    private String message;
}
