package br.com.cetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;

public class DetalhesActivity extends Activity {
    private Cliente cliente;
    private PagamentoRepository pagamentos;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_detalhes);
        findViewById(R.id.voltar).setOnClickListener(v -> finish());
        CetApplication app = (CetApplication) getApplication();
        pagamentos = app.getPagamentos();
        cliente = app.getClientes().porId(getIntent().getLongExtra("clienteId", -1));
        if (cliente == null) {
            Toast.makeText(this, "Cadastro indisponível. Os dados desta etapa são temporários.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.detalhe_nome)).setText(cliente.getNome());
        ((TextView) findViewById(R.id.detalhe_email)).setText(cliente.getEmail());
        ((TextView) findViewById(R.id.detalhe_telefone)).setText(cliente.getTelefone());
        findViewById(R.id.registrar_pagamento).setOnClickListener(v -> startActivity(
                new Intent(this, PagamentoActivity.class).putExtra("clienteId", cliente.getId())));
    }

    @Override protected void onResume() {
        super.onResume();
        if (cliente != null) atualizarSituacao();
    }

    private void atualizarSituacao() {
        Calendar hoje = Calendar.getInstance();
        int mes = hoje.get(Calendar.MONTH) + 1, ano = hoje.get(Calendar.YEAR);
        Pagamento atual = pagamentos.daCompetencia(cliente.getId(), mes, ano);
        ((TextView) findViewById(R.id.situacao_titulo)).setText("Mensalidade de " + Pagamento.competencia(mes, ano));
        TextView situacao = findViewById(R.id.situacao);
        TextView detalhe = findViewById(R.id.situacao_detalhe);
        if (atual == null) {
            situacao.setText("Pendente");
            situacao.setTextColor(0xFFA32626);
            detalhe.setText("Nenhum pagamento registrado para esta competência.");
        } else {
            situacao.setText("Paga");
            situacao.setTextColor(0xFF006C4C);
            detalhe.setText(Pagamento.formatarValor(atual.getValorCentavos()) + " em " + atual.getData());
        }

        List<Pagamento> lista = pagamentos.doCliente(cliente.getId());
        findViewById(R.id.historico_vazio).setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        LinearLayout historico = findViewById(R.id.historico);
        historico.removeAllViews();
        for (Pagamento p : lista) {
            View linha = LayoutInflater.from(this).inflate(R.layout.item_pagamento, historico, false);
            ((TextView) linha.findViewById(R.id.linha_titulo)).setText(
                    "Competência " + p.getCompetencia() + " • " + Pagamento.formatarValor(p.getValorCentavos()));
            ((TextView) linha.findViewById(R.id.linha_subtitulo)).setText("Pago em " + p.getData());
            historico.addView(linha);
        }
    }
}
