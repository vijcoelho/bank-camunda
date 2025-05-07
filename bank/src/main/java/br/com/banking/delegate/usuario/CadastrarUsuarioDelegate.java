package br.com.banking.delegate.usuario;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.dtos.response.CadastrarUsuarioResponse;
import br.com.banking.service.UsuarioService;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Named
public class CadastrarUsuarioDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public CadastrarUsuarioDelegate(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String cpf = (String) delegateExecution.getVariable("cpf");
        String nome = (String) delegateExecution.getVariable("nome");
        String senha = (String) delegateExecution.getVariable("senha");
        Integer idade = (Integer) delegateExecution.getVariable("idade");

        CadastrarUsuarioRequest request = CadastrarUsuarioRequest.builder()
                .nome(nome)
                .cpf(cpf)
                .senha(passwordEncoder.encode(senha))
                .idade(idade)
                .build();

        CadastrarUsuarioResponse response = usuarioService.cadastrar(request);
        delegateExecution.setVariable("id", response.getId());
        delegateExecution.setVariable("mensagem", response.getMensagem());
    }
}
