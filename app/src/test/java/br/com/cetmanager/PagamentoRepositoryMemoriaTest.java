package br.com.cetmanager;
import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;

public class PagamentoRepositoryMemoriaTest {
    private static Calendar dia(int dia, int mes, int ano) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(ano, mes - 1, dia, 12, 0);
        return c;
    }
    private final Calendar hoje = dia(29, 10, 2026);

    @Test public void registraNormalizaEConsulta() {
        ClienteRepository clientes = new ClienteRepositoryMemoria();
        PagamentoRepository r = new PagamentoRepositoryMemoria(clientes);
        Cliente ana = clientes.cadastrar("Ana", "ana@example.com", "6133334444");
        Pagamento p = r.registrar(ana.getId(), " 9/2026 ", "R$ 1.250,5", "5/9/2026", hoje);
        assertEquals("09/2026", p.getCompetencia());
        assertEquals(125050, p.getValorCentavos());
        assertEquals("05/09/2026", p.getData());
        assertEquals("R$ 1.250,50", Pagamento.formatarValor(p.getValorCentavos()));
        assertSame(p, r.daCompetencia(ana.getId(), 9, 2026));
        assertNull(r.daCompetencia(ana.getId(), 10, 2026));
        r.registrar(ana.getId(), "10/2026", "150.00", "29/10/2026", hoje);
        assertEquals("10/2026", r.doCliente(ana.getId()).get(0).getCompetencia());
        assertEquals(2, r.doCliente(ana.getId()).size());
    }

    @Test public void rejeitaEntradasInvalidasSemRegistrar() {
        ClienteRepository clientes = new ClienteRepositoryMemoria();
        PagamentoRepository r = new PagamentoRepositoryMemoria(clientes);
        long id = clientes.cadastrar("Ana", "ana@example.com", "6133334444").getId();
        String[][] dados = {
            {"13/2026", "150", "01/10/2026"}, {"10-2026", "150", "01/10/2026"},
            {"10/2020", "150", "01/10/2026"}, {"11/2026", "150", "01/10/2026"},
            {"10/2026", "0", "01/10/2026"}, {"10/2026", "abc", "01/10/2026"},
            {"10/2026", "10,999", "01/10/2026"}, {"10/2026", "100000", "01/10/2026"},
            {"10/2026", "150", "31/02/2026"}, {"10/2026", "150", "30/10/2026"}, {"10/2026", "150", ""}};
        for (String[] d : dados) {
            assertThrows(String.join(" ", d), IllegalArgumentException.class, () -> r.registrar(id, d[0], d[1], d[2], hoje));
        }
        assertThrows(IllegalArgumentException.class, () -> r.registrar(-1, "10/2026", "150", "01/10/2026", hoje));
        assertTrue(r.doCliente(id).isEmpty());
    }

    @Test public void rejeitaCompetenciaDuplicadaDoMesmoCliente() {
        ClienteRepository clientes = new ClienteRepositoryMemoria();
        PagamentoRepository r = new PagamentoRepositoryMemoria(clientes);
        long ana = clientes.cadastrar("Ana", "ana@example.com", "6133334444").getId();
        long bia = clientes.cadastrar("Bia", "bia@example.com", "6133335555").getId();
        r.registrar(ana, "10/2026", "150", "01/10/2026", hoje);
        assertThrows(IllegalArgumentException.class, () -> r.registrar(ana, "10/2026", "200", "02/10/2026", hoje));
        r.registrar(bia, "10/2026", "150", "01/10/2026", hoje);
        assertEquals(1, r.doCliente(ana).size());
    }

    @Test public void relatorioSeparaPagosEPendentesDaCompetencia() {
        ClienteRepository clientes = new ClienteRepositoryMemoria();
        PagamentoRepository r = new PagamentoRepositoryMemoria(clientes);
        Cliente ana = clientes.cadastrar("Ana", "ana@example.com", "6133334444");
        Cliente bia = clientes.cadastrar("Bia", "bia@example.com", "6133335555");
        clientes.cadastrar("Caio", "caio@example.com", "6133336666");
        r.registrar(ana.getId(), "10/2026", "150,00", "05/10/2026", hoje);
        r.registrar(bia.getId(), "10/2026", "99,90", "06/10/2026", hoje);
        r.registrar(bia.getId(), "09/2026", "99,90", "06/09/2026", hoje);
        RelatorioMensal outubro = new RelatorioMensal(clientes, r, 10, 2026);
        assertEquals(2, outubro.getPagos().size());
        assertEquals("Caio", outubro.getPendentes().get(0).getNome());
        assertEquals(24990, outubro.getTotalCentavos());
        RelatorioMensal setembro = new RelatorioMensal(clientes, r, 9, 2026);
        assertEquals(1, setembro.getPagos().size());
        assertEquals(2, setembro.getPendentes().size());
    }
}
