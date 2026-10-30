package br.com.cetmanager;

import android.app.Application;

public class CetApplication extends Application {
    private final ClienteRepository clientes = new ClienteRepositoryMemoria();
    private final PagamentoRepository pagamentos = new PagamentoRepositoryMemoria(clientes);
    public ClienteRepository getClientes() { return clientes; }
    public PagamentoRepository getPagamentos() { return pagamentos; }
}
