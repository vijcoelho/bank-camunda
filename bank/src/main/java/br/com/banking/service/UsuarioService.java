package br.com.banking.service;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.entity.Usuario;
import br.com.banking.entity.enums.StatusUsuario;
import br.com.banking.exceptions.UsusarioNaoEncontradoException;
import br.com.banking.mapper.UsuarioMapper;
import br.com.banking.repository.UsuarioRepository;
import br.com.banking.security.jwtconfig.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(UsuarioDTO request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        String senhaHash = passwordEncoder.encode(request.getSenha());
        
        usuario.setSaldo(BigDecimal.valueOf(0));
        usuario.setSenha(senhaHash);
        usuario.setStatus(StatusUsuario.ATIVO);

        return usuarioRepository.save(usuario);
    }

    public UsuarioDTO login(UsuarioDTO request) {
        if (request.getCpf() == null || request.getSenha() == null) {
            throw new IllegalArgumentException("CPF ou senha não podem ser nulos");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());
        if (usuarioOpt.isEmpty()) {
            throw new UsusarioNaoEncontradoException();
        }

        Usuario usuario = usuarioOpt.get();
        
        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuario.getSenha());
        if (!senhaCorreta) {
            throw new RuntimeException("Senha inválida! Tente novamente");
        }

        String token = jwtService.generateToken(usuario.getCpf());
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getStatus(),
                usuario.getSaldo(),
                token,
                "Usuário logado com sucesso!! Aproveite..."
        );
    }

    public UsuarioDTO depositar(UsuarioDTO request, String token) {
        if (request.getCpf() == null || request.getSenha() == null) {
            throw new IllegalArgumentException("CPF ou senha não podem ser nulos");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());
        if (usuarioOpt.isEmpty()) {
            throw new UsusarioNaoEncontradoException();
        }
        Usuario usuario = usuarioOpt.get();
        String jwtToken = token.replace("Bearer", "");
        String tokenDoCpf = jwtService.extractCpf(jwtToken);
        if (!usuario.getCpf().equals(tokenDoCpf)) {
            throw new RuntimeException("Token invalido para esse cpf: " + usuario.getCpf());
        }

        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuario.getSenha());
        if (!senhaCorreta) {
            throw new RuntimeException("Senha invalida! Tente novamente");
        }

        usuario.setSaldo(usuario.getSaldo().add(request.getSaldo()));
        usuarioRepository.save(usuario);
        return new UsuarioDTO(
                usuario.getCpf(),
                usuario.getSaldo()
        );
    }
}