package br.com.banking.controller;

import br.com.banking.dtos.request.CadastrarUsuarioRequest;
import br.com.banking.dtos.request.DepositarRequest;
import br.com.banking.dtos.request.LoginUsuarioRequest;
import br.com.banking.dtos.response.CadastrarUsuarioResponse;
import br.com.banking.dtos.response.LoginUsuarioResponse;
import br.com.banking.entity.Usuario;
import br.com.banking.service.UsuarioService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UsuarioController {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final UsuarioService usuarioService;

    public UsuarioController(RuntimeService runtimeService, TaskService taskService, UsuarioService usuarioService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.usuarioService = usuarioService;
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

    @PostMapping("/auth/login")
    public ResponseEntity<LoginUsuarioResponse> login(@RequestBody LoginUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(usuarioService.login(request));
    }

    @GetMapping("/usuario/pegar-pelo-cpf/{cpf}")
    public ResponseEntity<Usuario> pegarPeloCpf(@PathVariable("cpf") String cpf, @RequestHeader("Authorization") String token) {
        String tokenPuro = token.replace("Bearer ", "");
        Usuario usuario = usuarioService.pegarPeloCpf(cpf, tokenPuro);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/usuario/depositar")
    public ResponseEntity<Map<String, Object>> depositar(@RequestBody DepositarRequest request, @RequestHeader("Authorization") String token) {
        String tokenPuro = token.replace("Bearer ", "");

        Map<String, Object> vars = new HashMap<>();
        vars.put("cpf", request.getCpf());
        vars.put("senha", request.getSenha());
        vars.put("quantidade", request.getQuantidade());
        vars.put("token", tokenPuro);

        ProcessInstance instance = runtimeService.startProcessInstanceByMessage(
                "startProcessoDeposito",
                vars
        );

        return ResponseEntity.accepted().body(
                Map.of(
                        "mensagem", "Processo de deposito iniciado com sucesso, segue id do processo",
                        "idProcesso", instance.getId()
                )
        );
    }
}
