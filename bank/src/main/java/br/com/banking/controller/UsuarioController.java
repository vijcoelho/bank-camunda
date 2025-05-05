package br.com.banking.controller;

import br.com.banking.dtos.UsuarioDTO;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UsuarioController {
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public UsuarioController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @PostMapping("/auth/cadastrar")
    public ResponseEntity<?> cadastro(@RequestBody UsuarioDTO request) {
        try {
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                    "Process_19j8h40",
                    Map.of("opcao", 1)
            );

            Task primeriaTask = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .singleResult();
            if (primeriaTask != null) {
                taskService.complete(primeriaTask.getId());
            }

            Task cadastroTask = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .singleResult();
            if (cadastroTask != null) {
                Map<String, Object> variaveis = new HashMap<>();
                variaveis.put("nome", request.getNome());
                variaveis.put("cpf", request.getCpf());
                variaveis.put("senha", request.getSenha());

                taskService.complete(cadastroTask.getId(), variaveis);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cadastro realizado com sucesso!");
            response.put("redirect", "/auth/login");
            response.put("cadastroInfo", Map.of(
                    "cpf", request.getCpf(),
                    "processInstanceId", instance.getId()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erro no cadastro: " + e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO request) {
        try {
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                    "Process_19j8h40",
                    Map.of(
                            "opcao", 2,
                            "cpf", request.getCpf(),
                            "senha", request.getSenha()
                    )
            );

            Task firstTask = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .singleResult();
            if (firstTask != null) {
                taskService.complete(firstTask.getId());
            }

            Task loginTask = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .singleResult();
            if (loginTask != null) {
                taskService.complete(loginTask.getId());
            }

            Map<String, Object> processVariables = runtimeService.getVariables(instance.getId());
            Boolean loginOk = (Boolean) processVariables.get("loginOk");

            if (loginOk != null && loginOk) {
                String token = (String) processVariables.get("token");
                String message = (String) processVariables.get("message");
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "message", message != null ? message : "Login realizado com sucesso!"
                ));
            } else {
                String errorMessage = (String) processVariables.get("loginError");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", errorMessage != null ? errorMessage : "Credenciais inválidas"));
            }

        } catch (Exception e) {
            log.error("Erro no processo de login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro no login: " + e.getMessage()));
        }
    }
}