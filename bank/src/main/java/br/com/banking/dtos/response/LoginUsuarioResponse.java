package br.com.banking.dtos.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginUsuarioResponse {
    private String id;
    private String mensagem;
    private String token;
}
