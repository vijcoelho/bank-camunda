package br.com.banking.mapper;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.entity.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioMapper(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }
        if (dto.getNome() == null || dto.getCpf() == null || dto.getSenha() == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }
        return new Usuario(dto.getNome(), dto.getCpf(), passwordEncoder.encode(dto.getSenha()));
    }
}
