package br.com.cetmanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;

public class CadastroActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_cadastro);
        EditText nome = findViewById(R.id.nome);
        EditText email = findViewById(R.id.email);
        EditText telefone = findViewById(R.id.telefone);
        TextView erro = findViewById(R.id.erro);
        TextWatcher limparErro = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                erro.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) {}
        };
        nome.addTextChangedListener(limparErro);
        email.addTextChangedListener(limparErro);
        telefone.addTextChangedListener(limparErro);
        findViewById(R.id.voltar).setOnClickListener(v -> finish());
        findViewById(R.id.salvar).setOnClickListener(v -> {
            try {
                ((CetApplication) getApplication()).getClientes().cadastrar(
                    nome.getText().toString(), email.getText().toString(), telefone.getText().toString());
                v.setEnabled(false);
                Toast.makeText(this, "Cliente cadastrado nesta sessão.", Toast.LENGTH_LONG).show();
                finish();
            } catch (IllegalArgumentException e) {
                erro.setText(e.getMessage());
                erro.setVisibility(View.VISIBLE);
                erro.announceForAccessibility(e.getMessage());
            }
        });
    }
}
