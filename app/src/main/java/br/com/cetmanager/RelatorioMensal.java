package br.com.cetmanager;

import java.util.ArrayList;
import java.util.List;

/** Situação das mensalidades de uma competência: pendente é o cliente sem pagamento registrado. */
public final class RelatorioMensal {
    private final List<Cliente> pagos = new ArrayList<>(), pendentes = new ArrayList<>();
    private long totalCentavos;

    public RelatorioMensal(ClienteRepository clientes, PagamentoRepository pagamentos, int mes, int ano) {
        for (Cliente c : clientes.buscar("")) {
            Pagamento p = pagamentos.daCompetencia(c.getId(), mes, ano);
            if (p == null) {
                pendentes.add(c);
            } else {
                pagos.add(c);
                totalCentavos += p.getValorCentavos();
            }
        }
    }
    public List<Cliente> getPagos() { return pagos; }
    public List<Cliente> getPendentes() { return pendentes; }
    public long getTotalCentavos() { return totalCentavos; }
}
