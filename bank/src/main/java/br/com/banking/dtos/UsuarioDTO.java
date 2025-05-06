package br.com.banking.dtos;

import br.com.banking.entity.enums.StatusUsuario;

import java.math.BigDecimal;

public class UsuarioDTO {
    private String id;
    private String nome;
    private String cpf;
    private String senha;
    private StatusUsuario statusUsuario;
    private BigDecimal saldo;
    private String token;
    private String mensagem;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String firstParam, String secondParam, boolean isCredentials) {
        if (isCredentials) {
            this.cpf = firstParam;
            this.senha = secondParam;
        } else {
            this.id = firstParam;
            this.mensagem = secondParam;
        }
    }

    public UsuarioDTO(BigDecimal saldo, String senha, String cpf) {
        this.cpf = cpf;
        this.saldo = saldo;
        this.senha = senha;
    }

    public UsuarioDTO(String cpf, BigDecimal saldo) {
        this.cpf = cpf;
        this.saldo = saldo;
    }

    public UsuarioDTO(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
    }

    public UsuarioDTO(
            String id,
            String nome,
            String cpf,
            StatusUsuario statusUsuario,
            BigDecimal saldo,
            String token,
            String mensagem
    ) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.statusUsuario = statusUsuario;
        this.saldo = saldo;
        this.token = token;
        this.mensagem = mensagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public StatusUsuario getStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(StatusUsuario statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
