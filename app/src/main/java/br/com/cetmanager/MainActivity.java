package br.com.cetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.*;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {
    private EditText busca;
    private ListView lista;
    private TextView contador, vazio;
    private ClienteRepository repository;
    private PagamentoRepository pagamentos;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        repository = ((CetApplication) getApplication()).getClientes();
        pagamentos = ((CetApplication) getApplication()).getPagamentos();
        busca = findViewById(R.id.busca);
        lista = findViewById(R.id.lista);
        contador = findViewById(R.id.contador);
        vazio = findViewById(R.id.vazio);
        lista.setEmptyView(vazio);
        findViewById(R.id.novo).setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        findViewById(R.id.relatorio).setOnClickListener(v -> startActivity(new Intent(this, RelatorioActivity.class)));
        lista.setOnItemClickListener((parent, view, position, id) -> {
            Cliente c = (Cliente) parent.getItemAtPosition(position);
            startActivity(new Intent(this, DetalhesActivity.class).putExtra("clienteId", c.getId()));
        });
        busca.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { atualizar(); }
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override protected void onResume() { super.onResume(); atualizar(); }

    private void atualizar() {
        List<Cliente> clientes = repository.buscar(busca.getText().toString());
        contador.setText(getString(R.string.count, clientes.size()));
        vazio.setText(repository.buscar("").isEmpty() ? R.string.empty : R.string.no_results);
        Calendar hoje = Calendar.getInstance();
        int mes = hoje.get(Calendar.MONTH) + 1, ano = hoje.get(Calendar.YEAR);
        lista.setAdapter(new ArrayAdapter<Cliente>(this, R.layout.item_cliente, clientes) {
            @Override public View getView(int position, View view, ViewGroup parent) {
                if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.item_cliente, parent, false);
                Cliente cliente = getItem(position);
                ((TextView) view.findViewById(R.id.nome_item)).setText(cliente.getNome());
                ((TextView) view.findViewById(R.id.email_item)).setText(cliente.getEmail());
                boolean paga = pagamentos.daCompetencia(cliente.getId(), mes, ano) != null;
                TextView situacao = view.findViewById(R.id.situacao_item);
                situacao.setText("Mensalidade " + Pagamento.competencia(mes, ano) + ": " + (paga ? "paga" : "pendente"));
                situacao.setTextColor(paga ? 0xFF006C4C : 0xFFA32626);
                return view;
            }
        });
    }
}
