package br.com.cetmanager;

import android.app.Application;

public class CetApplication extends Application {
    private final ClienteRepository clientes = new ClienteRepositoryMemoria();
    public ClienteRepository getClientes() { return clientes; }
}
