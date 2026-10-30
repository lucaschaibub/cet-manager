package br.com.cetmanager;

import java.util.Locale;

/** Pagamento de uma mensalidade (competência MM/AAAA) associado a um cliente. */
public final class Pagamento {
    private final long id, clienteId, valorCentavos;
    private final int mes, ano;
    private final String data;
    public Pagamento(long id, long clienteId, int mes, int ano, long valorCentavos, String data) {
        this.id = id; this.clienteId = clienteId; this.mes = mes; this.ano = ano;
        this.valorCentavos = valorCentavos; this.data = data;
    }
    public long getId() { return id; }
    public long getClienteId() { return clienteId; }
    public int getMes() { return mes; }
    public int getAno() { return ano; }
    public long getValorCentavos() { return valorCentavos; }
    public String getData() { return data; }
    public String getCompetencia() { return competencia(mes, ano); }

    public static String competencia(int mes, int ano) {
        return String.format(Locale.ROOT, "%02d/%04d", mes, ano);
    }

    /** Formata centavos no padrão brasileiro, por exemplo R$ 1.250,00. */
    public static String formatarValor(long centavos) {
        String reais = String.format(Locale.ROOT, "%,d", centavos / 100).replace(',', '.');
        return "R$ " + reais + "," + String.format(Locale.ROOT, "%02d", centavos % 100);
    }
}
