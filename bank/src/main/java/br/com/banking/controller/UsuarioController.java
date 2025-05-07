package br.com.banking.controller;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.dtos.response.CadastrarUsuarioResponse;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UsuarioController {
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public UsuarioController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @PostMapping("/auth/cadastrar")
    public ResponseEntity<CadastrarUsuarioResponse> cadastro(@RequestBody CadastrarUsuarioRequest request) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("nome", request.getNome());
        vars.put("cpf", request.getCpf());
        vars.put("senha", request.getSenha());
        vars.put("idade", request.getIdade());

        ProcessInstance instance = runtimeService.startProcessInstanceByMessage(
                "startProcessoConta",
                vars
        );
        
        return ResponseEntity.accepted()
                .header("Location", "/process-status/" + instance.getId())
                .body(CadastrarUsuarioResponse.builder()
                        .mensagem("Processo em andamento")
                        .id(instance.getId())
                        .build());
    }
}
