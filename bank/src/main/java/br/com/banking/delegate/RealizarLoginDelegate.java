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

        log.info("RealizarLoginDelegate - Iniciando processo de login");
        log.info("CPF recebido: {}", cpf);

        try {
            UsuarioDTO request = new UsuarioDTO();
            request.setCpf(cpf);
            request.setSenha(senha);

            log.info("Chamando usuarioService.login()");
            UsuarioDTO response = usuarioService.login(request);

            // Se chegou até aqui, o login foi bem sucedido
            log.info("Login realizado com sucesso para usuário: {}", response.getNome());
            execution.setVariable("loginOk", true);
            execution.setVariable("token", response.getToken());
            execution.setVariable("usuarioId", response.getId());
            execution.setVariable("usuarioNome", response.getNome());
            execution.setVariable("usuarioSaldo", response.getSaldo());
            execution.setVariable("message", response.getMensagem());
            execution.setVariable("loginError", null); // Limpa qualquer erro anterior

        } catch (Exception e) {
            log.error("Erro no processo de login", e);
            execution.setVariable("loginOk", false);
            execution.setVariable("loginError", "Credenciais inválidas");
            execution.setVariable("token", null);
            throw e; // Propaga a exceção para que o processo siga o fluxo de erro
        }
    }

}