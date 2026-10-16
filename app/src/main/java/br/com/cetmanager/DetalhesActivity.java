package br.com.cetmanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DetalhesActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_detalhes);
        findViewById(R.id.voltar).setOnClickListener(v -> finish());
        Cliente c = ((CetApplication) getApplication()).getClientes().porId(getIntent().getLongExtra("clienteId", -1));
        if (c == null) {
            Toast.makeText(this, "Cadastro indisponível. Os dados desta etapa são temporários.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.detalhe_nome)).setText(c.getNome());
        ((TextView) findViewById(R.id.detalhe_email)).setText(c.getEmail());
        ((TextView) findViewById(R.id.detalhe_telefone)).setText(c.getTelefone());
    }
}
