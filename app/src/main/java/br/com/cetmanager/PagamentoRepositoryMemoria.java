package br.com.cetmanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Pagamentos compartilhados pelas telas, somente enquanto o processo do app existir. */
public final class PagamentoRepositoryMemoria implements PagamentoRepository {
    /** Novembro de 2020: início das atividades da CET, conforme a documentação. */
    private static final int PRIMEIRA_COMPETENCIA = 2020 * 12 + 11;
    private static final Pattern COMPETENCIA = Pattern.compile("^(\\d{1,2})/(\\d{4})$");
    private static final Pattern DATA = Pattern.compile("^(\\d{1,2})/(\\d{1,2})/(\\d{4})$");
    private static final Pattern VALOR = Pattern.compile("^\\d+(\\.\\d+)?$");
    private final ClienteRepository clientes;
    private final List<Pagamento> pagamentos = new ArrayList<>();
    private long proximoId = 1;

    public PagamentoRepositoryMemoria(ClienteRepository clientes) { this.clientes = clientes; }

    @Override public Pagamento registrar(long clienteId, String competencia, String valor, String data, Calendar hoje) {
        if (clientes.porId(clienteId) == null)
            throw new IllegalArgumentException("Cliente não encontrado nesta sessão.");
        int mesAtual = hoje.get(Calendar.MONTH) + 1, anoAtual = hoje.get(Calendar.YEAR);

        Matcher c = COMPETENCIA.matcher(competencia == null ? "" : competencia.trim());
        int mes = c.matches() ? Integer.parseInt(c.group(1)) : 0;
        if (mes < 1 || mes > 12)
            throw new IllegalArgumentException("Informe a competência no formato MM/AAAA.");
        int ano = Integer.parseInt(c.group(2));
        if (ano * 12 + mes < PRIMEIRA_COMPETENCIA || ano * 12 + mes > anoAtual * 12 + mesAtual)
            throw new IllegalArgumentException("A competência deve estar entre 11/2020 e "
                    + Pagamento.competencia(mesAtual, anoAtual) + ".");

        long centavos = centavos(valor);
        if (centavos <= 0)
            throw new IllegalArgumentException("Informe um valor entre R$ 0,01 e R$ 99.999,99.");

        Calendar dia = dataValida(data);
        if (dia == null)
            throw new IllegalArgumentException("Informe a data do pagamento no formato DD/MM/AAAA.");
        if (dia.after(hoje))
            throw new IllegalArgumentException("A data do pagamento não pode ser futura.");

        if (daCompetencia(clienteId, mes, ano) != null)
            throw new IllegalArgumentException("Já existe pagamento registrado para esta competência.");
        String dataFormatada = String.format(Locale.ROOT, "%02d/%02d/%04d",
                dia.get(Calendar.DAY_OF_MONTH), dia.get(Calendar.MONTH) + 1, dia.get(Calendar.YEAR));
        Pagamento pagamento = new Pagamento(proximoId++, clienteId, mes, ano, centavos, dataFormatada);
        pagamentos.add(pagamento);
        return pagamento;
    }

    /** Mais recentes primeiro. */
    @Override public List<Pagamento> doCliente(long clienteId) {
        List<Pagamento> resultado = new ArrayList<>();
        for (Pagamento p : pagamentos) if (p.getClienteId() == clienteId) resultado.add(p);
        Collections.sort(resultado, (a, b) -> (b.getAno() * 12 + b.getMes()) - (a.getAno() * 12 + a.getMes()));
        return resultado;
    }

    @Override public Pagamento daCompetencia(long clienteId, int mes, int ano) {
        for (Pagamento p : pagamentos)
            if (p.getClienteId() == clienteId && p.getMes() == mes && p.getAno() == ano) return p;
        return null;
    }

    /** Aceita "150", "150,5", "1.250,00" ou "150.00". Retorna -1 se o texto for inválido. */
    private static long centavos(String valor) {
        String v = valor == null ? "" : valor.replace("R$", "").replace(" ", "").trim();
        if (v.contains(",")) v = v.replace(".", "").replace(',', '.');
        if (!VALOR.matcher(v).matches()) return -1;
        BigDecimal decimal = new BigDecimal(v);
        if (decimal.scale() > 2 || decimal.compareTo(new BigDecimal("100000")) >= 0) return -1;
        return decimal.movePointRight(2).longValueExact();
    }

    private static Calendar dataValida(String data) {
        Matcher m = DATA.matcher(data == null ? "" : data.trim());
        if (!m.matches()) return null;
        Calendar dia = Calendar.getInstance();
        dia.clear();
        dia.setLenient(false);
        try {
            dia.set(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(1)));
            dia.getTimeInMillis();
            return dia;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
