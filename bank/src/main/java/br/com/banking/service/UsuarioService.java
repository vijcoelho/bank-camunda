package br.com.banking.service;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.dtos.response.CadastrarUsuarioResponse;
import br.com.banking.entity.Usuario;
import br.com.banking.mapper.UsuarioMapper;
import br.com.banking.repository.UsuarioRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public CadastrarUsuarioResponse cadastrar(CadastrarUsuarioRequest request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        usuarioRepository.save(usuario);
        return CadastrarUsuarioResponse.builder()
                .id(usuario.getId())
                .mensagem("Usuario cadastrado com sucesso!")
                .build();
    }

    public Boolean validarConta(String cpf, Integer idade) {
        Optional<Usuario> usuario = usuarioRepository.findByCpf(cpf);
        if (usuario.isPresent()) return false;
        if (idade < 18) return false;
        return true;
    }
}
