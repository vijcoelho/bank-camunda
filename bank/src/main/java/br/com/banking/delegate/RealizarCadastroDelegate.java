package br.com.banking.delegate;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.service.UsuarioService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("realizarCadastroDelegate")
public class RealizarCadastroDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public RealizarCadastroDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String nome = execution.getVariable("nome").toString();
        String cpf = execution.getVariable("cpf").toString();
        String senha = execution.getVariable("senha").toString();

        UsuarioDTO usuarioDTO = new UsuarioDTO(nome, cpf, senha);

        usuarioService.cadastrar(usuarioDTO);
        System.out.println("usuario cadastrado com sucesso!");
    }
}
