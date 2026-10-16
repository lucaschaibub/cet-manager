package br.com.cetmanager;

import java.util.List;

/** Na parte 2, uma implementação com banco poderá substituir a versão em memória. */
public interface ClienteRepository {
    Cliente cadastrar(String nome, String email, String telefone);
    List<Cliente> buscar(String termo);
    Cliente porId(long id);
}
