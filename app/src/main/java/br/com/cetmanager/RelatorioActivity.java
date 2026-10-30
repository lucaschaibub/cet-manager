package br.com.cetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.List;

public class RelatorioActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_relatorio);
        findViewById(R.id.voltar).setOnClickListener(v -> finish());
    }

    @Override protected void onResume() {
        super.onResume();
        CetApplication app = (CetApplication) getApplication();
        Calendar hoje = Calendar.getInstance();
        int mes = hoje.get(Calendar.MONTH) + 1, ano = hoje.get(Calendar.YEAR);
        RelatorioMensal relatorio = new RelatorioMensal(app.getClientes(), app.getPagamentos(), mes, ano);
        List<Cliente> pagos = relatorio.getPagos(), pendentes = relatorio.getPendentes();

        ((TextView) findViewById(R.id.relatorio_competencia)).setText("Competência " + Pagamento.competencia(mes, ano));
        ((TextView) findViewById(R.id.resumo)).setText(
                "Clientes cadastrados: " + (pagos.size() + pendentes.size())
                + "\nMensalidades pagas: " + pagos.size()
                + "\nMensalidades pendentes: " + pendentes.size()
                + "\nTotal recebido: " + Pagamento.formatarValor(relatorio.getTotalCentavos()));
        ((TextView) findViewById(R.id.titulo_pendentes)).setText("Pendentes (" + pendentes.size() + ")");
        ((TextView) findViewById(R.id.titulo_pagos)).setText("Pagas (" + pagos.size() + ")");
        preencher(R.id.pendentes, R.id.pendentes_vazio, pendentes, null, mes, ano);
        preencher(R.id.pagos, R.id.pagos_vazio, pagos, app.getPagamentos(), mes, ano);
    }

    private void preencher(int listaId, int vazioId, List<Cliente> clientes, PagamentoRepository pagamentos, int mes, int ano) {
        findViewById(vazioId).setVisibility(clientes.isEmpty() ? View.VISIBLE : View.GONE);
        LinearLayout lista = findViewById(listaId);
        lista.removeAllViews();
        for (Cliente c : clientes) {
            View linha = LayoutInflater.from(this).inflate(R.layout.item_pagamento, lista, false);
            ((TextView) linha.findViewById(R.id.linha_titulo)).setText(c.getNome());
            String subtitulo = c.getEmail();
            if (pagamentos != null) {
                Pagamento p = pagamentos.daCompetencia(c.getId(), mes, ano);
                subtitulo = Pagamento.formatarValor(p.getValorCentavos()) + " pago em " + p.getData();
            }
            ((TextView) linha.findViewById(R.id.linha_subtitulo)).setText(subtitulo);
            linha.setOnClickListener(v -> startActivity(
                    new Intent(this, DetalhesActivity.class).putExtra("clienteId", c.getId())));
            lista.addView(linha);
        }
    }
}
