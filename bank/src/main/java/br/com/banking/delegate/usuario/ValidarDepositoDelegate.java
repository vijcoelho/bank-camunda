package br.com.banking.delegate.usuario;

import br.com.banking.service.UsuarioService;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class ValidarDepositoDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public ValidarDepositoDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String cpf = (String) delegateExecution.getVariable("cpf");
        String senha = (String) delegateExecution.getVariable("senha");

        boolean validar = usuarioService.validarDeposito(cpf, senha);
        if (!validar) {
            throw new BpmnError(
                    "deposito_invalido", "Cpf errado ou senha errada"
            );
        }
    }
}
