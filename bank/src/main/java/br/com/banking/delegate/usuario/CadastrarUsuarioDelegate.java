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

    public CadastrarUsuarioDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
                .senha(senha)
                .idade(idade)
                .build();

        usuarioService.cadastrar(request);
    }
}
