package br.com.banking.mapper;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toEntity(CadastrarUsuarioRequest request) {
        return Usuario.builder()
                .nome(request.getNome())
                .idade(request.getIdade())
                .cpf(request.getCpf())
                .senha(request.getSenha())
                .build();
    }
}
