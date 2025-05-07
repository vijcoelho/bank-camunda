package br.com.banking.dtos.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginUsuarioRequest {
    private String cpf;
    private String senha;
}
