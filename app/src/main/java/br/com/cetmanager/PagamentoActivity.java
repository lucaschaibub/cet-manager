package br.com.cetmanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Locale;

public class PagamentoActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_pagamento);
        CetApplication app = (CetApplication) getApplication();
        long clienteId = getIntent().getLongExtra("clienteId", -1);
        Cliente cliente = app.getClientes().porId(clienteId);
        if (cliente == null) {
            Toast.makeText(this, "Cadastro indisponível. Os dados desta etapa são temporários.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.pagamento_cliente)).setText(cliente.getNome());
        EditText competencia = findViewById(R.id.competencia);
        EditText valor = findViewById(R.id.valor);
        EditText data = findViewById(R.id.data_pagamento);
        TextView erro = findViewById(R.id.erro);
        Calendar hoje = Calendar.getInstance();
        if (state == null) {
            competencia.setText(Pagamento.competencia(hoje.get(Calendar.MONTH) + 1, hoje.get(Calendar.YEAR)));
            data.setText(String.format(Locale.ROOT, "%02d/%02d/%04d",
                    hoje.get(Calendar.DAY_OF_MONTH), hoje.get(Calendar.MONTH) + 1, hoje.get(Calendar.YEAR)));
        }
        TextWatcher limparErro = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                erro.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        };
        competencia.addTextChangedListener(limparErro);
        valor.addTextChangedListener(limparErro);
        data.addTextChangedListener(limparErro);
        findViewById(R.id.voltar).setOnClickListener(v -> finish());
        findViewById(R.id.salvar).setOnClickListener(v -> {
            try {
                app.getPagamentos().registrar(clienteId, competencia.getText().toString(),
                        valor.getText().toString(), data.getText().toString(), Calendar.getInstance());
                v.setEnabled(false);
                Toast.makeText(this, "Pagamento registrado nesta sessão.", Toast.LENGTH_LONG).show();
                finish();
            } catch (IllegalArgumentException e) {
                erro.setText(e.getMessage());
                erro.setVisibility(View.VISIBLE);
                erro.announceForAccessibility(e.getMessage());
            }
        });
    }
}
