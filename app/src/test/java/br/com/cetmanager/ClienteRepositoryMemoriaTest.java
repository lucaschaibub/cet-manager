package br.com.cetmanager;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClienteRepositoryMemoriaTest {
    @Test public void cadastraNormalizaEConsulta() {
        ClienteRepository r = new ClienteRepositoryMemoria();
        Cliente c = r.cadastrar("  João Silva  ", "JOAO@example.com ", "(61) 99999-1234");
        assertEquals("João Silva", c.getNome());
        assertEquals("joao@example.com", c.getEmail());
        assertEquals("61999991234", c.getTelefone());
        assertSame(c, r.porId(c.getId()));
        assertEquals(1, r.buscar("JOAO").size());
        assertEquals(1, r.buscar("example").size());
        assertTrue(r.buscar("inexistente").isEmpty());
    }
    @Test public void rejeitaEntradasInvalidasSemCadastrar() {
        ClienteRepository r = new ClienteRepositoryMemoria();
        String[][] dados = {{"", "a@b.com", "61999991234"}, {"Ana", "invalido", "61999991234"}, {"Ana", "a@b.com", "123"}};
        for (String[] d : dados) {
            assertThrows(IllegalArgumentException.class, () -> r.cadastrar(d[0], d[1], d[2]));
        }
        assertTrue(r.buscar("").isEmpty());
    }
    @Test public void rejeitaEmailDuplicado() {
        ClienteRepository r = new ClienteRepositoryMemoria();
        r.cadastrar("Ana", "ana@example.com", "6133334444");
        assertThrows(IllegalArgumentException.class, () -> r.cadastrar("Outra Ana", " ANA@EXAMPLE.COM ", "61999991234"));
        assertEquals(1, r.buscar("").size());
    }
    @Test public void resultadoDaBuscaNaoAlteraRepositorioENovaSessaoVazia() {
        ClienteRepository r = new ClienteRepositoryMemoria();
        r.cadastrar("Ana", "ana@example.com", "6133334444");
        r.buscar("").clear();
        assertEquals(1, r.buscar("").size());
        assertNull(r.porId(-1));
        assertTrue(new ClienteRepositoryMemoria().buscar("").isEmpty());
    }
}
