package br.com.banking.delegate;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.service.UsuarioService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("realizarDepositoDelegate")
public class RealizarDepositoDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public RealizarDepositoDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String cpf = (String) execution.getVariable("cpf");
        String senha = (String) execution.getVariable("senha");
        Long saldo = (Long) execution.getVariable("quantidade");
        String token = (String) execution.getVariable("token");

        BigDecimal saldoEmBigDecimal = new BigDecimal(saldo);

        usuarioService.depositar(new UsuarioDTO(saldoEmBigDecimal, senha, cpf), token);
    }
}
