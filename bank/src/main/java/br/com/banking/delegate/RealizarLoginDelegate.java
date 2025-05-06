package br.com.banking.delegate;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("realizarLoginDelegate")
public class RealizarLoginDelegate implements JavaDelegate {
    private final UsuarioService usuarioService;

    public RealizarLoginDelegate(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String cpf = (String) execution.getVariable("cpf");
        String senha = (String) execution.getVariable("senha");

        try {
            UsuarioDTO request = new UsuarioDTO();
            request.setCpf(cpf);
            request.setSenha(senha);

            UsuarioDTO response = usuarioService.login(request);

            execution.setVariable("loginOk", true);
            execution.setVariable("token", response.getToken());
            execution.setVariable("usuarioId", response.getId());
            execution.setVariable("usuarioNome", response.getNome());
            execution.setVariable("usuarioSaldo", response.getSaldo());
            execution.setVariable("message", response.getMensagem());
            execution.setVariable("loginError", null);

        } catch (Exception e) {
            execution.setVariable("loginOk", false);
            execution.setVariable("loginError", "Credenciais inválidas");
            execution.setVariable("token", null);
            throw e;
        }
    }

}