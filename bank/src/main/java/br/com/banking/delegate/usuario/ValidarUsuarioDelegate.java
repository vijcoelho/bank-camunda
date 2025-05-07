package br.com.banking.delegate.usuario;

import br.com.banking.service.UsuarioService;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class ValidarUsuarioDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public ValidarUsuarioDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String cpf = (String) delegateExecution.getVariable("cpf");
        Integer idade = (Integer) delegateExecution.getVariable("idade");

        boolean contaValida = usuarioService.validarConta(cpf, idade);
        if (!contaValida) {
            throw new BpmnError(
                    "conta_invalida", "Conta ja foi criada com esse cpf ou voce nao possui idade suficiente"
            );
        }
     }
}
