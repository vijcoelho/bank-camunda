package br.com.banking.dtos.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CadastrarUsuarioRequest {
    private String nome;
    private Integer idade;
    private String cpf;
    private String senha;
}
