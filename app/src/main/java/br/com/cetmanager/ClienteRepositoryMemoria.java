package br.com.cetmanager;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Dados compartilhados pelas telas, somente enquanto o processo do app existir. */
public final class ClienteRepositoryMemoria implements ClienteRepository {
    private final List<Cliente> clientes = new ArrayList<>();
    private long proximoId = 1;
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override public Cliente cadastrar(String nome, String email, String telefone) {
        nome = nome == null ? "" : nome.trim();
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        telefone = telefone == null ? "" : telefone.replaceAll("[^0-9]", "");
        if (nome.length() < 3 || nome.length() > 100)
            throw new IllegalArgumentException("Informe um nome entre 3 e 100 caracteres.");
        if (email.length() > 120 || !EMAIL.matcher(email).matches())
            throw new IllegalArgumentException("Informe um e-mail válido.");
        if (telefone.length() != 10 && telefone.length() != 11)
            throw new IllegalArgumentException("Informe um telefone com DDD, com 10 ou 11 dígitos.");
        for (Cliente c : clientes) if (c.getEmail().equalsIgnoreCase(email))
            throw new IllegalArgumentException("Já existe um cliente com este e-mail.");
        Cliente cliente = new Cliente(proximoId++, nome, email, telefone);
        clientes.add(cliente);
        return cliente;
    }

    @Override public List<Cliente> buscar(String termo) {
        String busca = normalizar(termo == null ? "" : termo.trim());
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : clientes)
            if (normalizar(c.getNome()).contains(busca) || normalizar(c.getEmail()).contains(busca))
                resultado.add(c);
        return resultado;
    }

    @Override public Cliente porId(long id) {
        for (Cliente c : clientes) if (c.getId() == id) return c;
        return null;
    }

    private static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
