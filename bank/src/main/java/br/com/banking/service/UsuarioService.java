package br.com.banking.service;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.dtos.request.DepositarRequest;
import br.com.banking.dtos.request.LoginUsuarioRequest;
import br.com.banking.dtos.response.CadastrarUsuarioResponse;
import br.com.banking.dtos.response.LoginUsuarioResponse;
import br.com.banking.entity.Usuario;
import br.com.banking.entity.enums.StatusUsuario;
import br.com.banking.mapper.UsuarioMapper;
import br.com.banking.repository.UsuarioRepository;
import br.com.banking.security.jwtconfig.JwtService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public CadastrarUsuarioResponse cadastrar(CadastrarUsuarioRequest request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setSaldo(BigDecimal.ZERO);
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

    public LoginUsuarioResponse login(LoginUsuarioRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());
        if (usuarioOpt.isEmpty()) {
            return LoginUsuarioResponse.builder()
                    .mensagem("Cpf nao encontrado no banco de dados")
                    .build();
        }

        if (usuarioOpt.get().getSenha() == null || usuarioOpt.get().getSenha().isBlank()) {
            return LoginUsuarioResponse.builder()
                    .mensagem("Senha do usuário está vazia no banco de dados!")
                    .build();
        }

        if (!passwordEncoder.matches(request.getSenha(), usuarioOpt.get().getSenha())) {
            return LoginUsuarioResponse.builder()
                    .mensagem("Senha incorreta!")
                    .build();
        }

        String jwtToken = jwtService.generateToken(usuarioOpt.get().getCpf());

        return LoginUsuarioResponse.builder()
                .id(usuarioOpt.get().getId())
                .mensagem("Usuario logado com sucesso!")
                .token(jwtToken)
                .build();
    }

    public Usuario pegarPeloCpf(String cpf, String token) {
        String jwtToken = jwtService.extractCpf(token);
        if (jwtToken.isBlank()) {
            throw new RuntimeException("Token esta vazio");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        return usuarioOpt.orElse(null);
    }

    public Usuario depositar(DepositarRequest request, String token) {
        String jwtToken = jwtService.extractCpf(token);
        if (jwtToken.isBlank()) {
            throw new RuntimeException("Token esta vazio");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());
        usuarioOpt.get().setSaldo(usuarioOpt.get().getSaldo().add(request.getQuantidade()));
        return usuarioRepository.save(usuarioOpt.get());
    }

    public Boolean validarDeposito(String cpf, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        if (usuarioOpt.isEmpty()) return false;
        if (!passwordEncoder.matches(senha, usuarioOpt.get().getSenha())) return false;
        return true;
    }
}
