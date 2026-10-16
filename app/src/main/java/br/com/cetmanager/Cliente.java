package br.com.cetmanager;

/** Modelo da primeira entrega. Não possui dados de pagamento. */
public final class Cliente {
    private final long id;
    private final String nome, email, telefone;
    public Cliente(long id, String nome, String email, String telefone) {
        this.id = id; this.nome = nome; this.email = email; this.telefone = telefone;
    }
    public long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
}
