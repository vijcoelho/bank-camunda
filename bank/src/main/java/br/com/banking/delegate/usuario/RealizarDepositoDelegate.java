package br.com.banking.delegate.usuario;

import br.com.banking.dtos.request.DepositarRequest;
import br.com.banking.service.UsuarioService;
import jakarta.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.math.BigDecimal;

@Named
public class RealizarDepositoDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public RealizarDepositoDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String cpf = (String) delegateExecution.getVariable("cpf");
        String senha = (String) delegateExecution.getVariable("senha");
        BigDecimal quantidade = (BigDecimal) delegateExecution.getVariable("quantidade");
        String token = (String) delegateExecution.getVariable("token");

        DepositarRequest request = DepositarRequest.builder()
                .cpf(cpf)
                .senha(senha)
                .quantidade(quantidade)
                .build();

        usuarioService.depositar(request, token);
    }
}
