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

        log.info("Cadastrando novo usuário: {}", request.getNome());
        log.info("Senha original: {}", request.getSenha());
        
        String senhaHash = passwordEncoder.encode(request.getSenha());
        log.info("Senha hash gerada: {}", senhaHash);
        
        usuario.setSaldo(BigDecimal.valueOf(0));
        usuario.setSenha(senhaHash);
        usuario.setStatus(StatusUsuario.ATIVO);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário cadastrado com sucesso. Testando senha...");

        boolean testeSenha = passwordEncoder.matches(request.getSenha(), usuarioSalvo.getSenha());
        log.info("Teste de senha pós-cadastro: {}", testeSenha);
        
        return usuarioSalvo;
    }

    public UsuarioDTO login(UsuarioDTO request) {
        if (request.getCpf() == null || request.getSenha() == null) {
            throw new IllegalArgumentException("CPF ou senha não podem ser nulos");
        }

        log.info("Tentativa de login para CPF: {}", request.getCpf());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());
        if (usuarioOpt.isEmpty()) {
            throw new UsusarioNaoEncontradoException();
        }

        Usuario usuario = usuarioOpt.get();
        
        log.info("Usuário encontrado: {}", usuario.getNome());
        log.info("Senha fornecida (raw): {}", request.getSenha());
        log.info("Senha armazenada (hash): {}", usuario.getSenha());
        
        boolean senhaCorreta = passwordEncoder.matches(request.getSenha(), usuario.getSenha());
        log.info("Resultado da verificação da senha: {}", senhaCorreta);

        if (!senhaCorreta) {
            log.warn("Senha inválida para usuário: {}", usuario.getNome());
            throw new RuntimeException("Senha inválida! Tente novamente");
        }

        log.info("Login bem sucedido para usuário: {}", usuario.getNome());

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
}