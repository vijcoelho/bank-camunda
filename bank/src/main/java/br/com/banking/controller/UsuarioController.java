package br.com.banking.controller;

import br.com.banking.dtos.UsuarioDTO;
import br.com.banking.entity.Usuario;
import br.com.banking.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class UsuarioController {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(RuntimeService runtimeService, TaskService taskService, UsuarioRepository usuarioRepository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.usuarioRepository = usuarioRepository;
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

            Task primeiraTask = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .singleResult();
            if (primeiraTask != null) {
                taskService.complete(primeiraTask.getId());
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
                        "mensagem", message != null ? message : "Login realizado com sucesso!"
                ));
            } else {
                String errorMessage = (String) processVariables.get("loginError");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", errorMessage != null ? errorMessage : "Credenciais inválidas"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro no login: " + e.getMessage()));
        }
    }

    @PatchMapping("/usuario/depositar")
    public ResponseEntity<?> deposito(@RequestBody UsuarioDTO request, @RequestHeader("Authorization") String token) {
        try {
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .variableValueEquals("cpf", request.getCpf())
                    .active()
                    .singleResult();

            if (instance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "erro", "Sessão não encontrada. Faça login novamente"
                        ));
            }

            Task taskAtual = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .taskDefinitionKey("Activity_14e0gpi")
                    .singleResult();

            if (taskAtual == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "erro", "Nenhuma tarefa pendente encontrada"
                        ));
            }

            Map<String, Object> variaveis = new HashMap<>();
            variaveis.put("opcaoBanco", 1);
            variaveis.put("cpf", request.getCpf());
            variaveis.put("senha", request.getSenha());
            variaveis.put("quantidade", request.getSaldo().toBigInteger().longValue());

            taskService.complete(taskAtual.getId(), variaveis);

            Map<String, Object> processVariables = runtimeService.getVariables(instance.getId());
            System.out.println("Variáveis do processo após a tarefa: " + processVariables);

            if (processVariables.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("erro", "Nenhuma informação do cliente recuperada"));
            }

            String cpf = (String) processVariables.get("cpf");
            Long saldo = (Long) processVariables.get("quantidade");

            Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(request.getCpf());

            Task taskDados = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .taskDefinitionKey("Activity_1oiyjaz")
                    .singleResult();
            if (taskDados != null) {
                taskService.complete(taskDados.getId(), variaveis);
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "cpf", cpf,
                            "novo_saldo", usuarioOpt.get().getSaldo().add(BigDecimal.valueOf(saldo))
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro no depósito: " + e.getMessage()));
        }
    }
}