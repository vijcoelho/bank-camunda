package br.com.banking.exceptions;

public class UsusarioNaoEncontradoException extends RuntimeException {
    public UsusarioNaoEncontradoException() {
        super("Usuario nao encontrado no banco de dados! Tente um cpf cadastrado!");
    }
}
