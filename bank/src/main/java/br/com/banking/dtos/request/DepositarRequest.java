package br.com.banking.dtos.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class DepositarRequest {
    private BigDecimal quantidade;
    private String cpf;
    private String senha;
}
