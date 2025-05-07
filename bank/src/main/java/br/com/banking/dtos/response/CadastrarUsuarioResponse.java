package br.com.banking.dtos.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CadastrarUsuarioResponse {
    private String id;
    private String mensagem;
}
