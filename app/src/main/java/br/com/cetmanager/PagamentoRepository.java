package br.com.cetmanager;

import java.util.Calendar;
import java.util.List;

/** Assim como ClienteRepository, poderá ser substituído por uma implementação com banco. */
public interface PagamentoRepository {
    Pagamento registrar(long clienteId, String competencia, String valor, String data, Calendar hoje);
    List<Pagamento> doCliente(long clienteId);
    Pagamento daCompetencia(long clienteId, int mes, int ano);
}
