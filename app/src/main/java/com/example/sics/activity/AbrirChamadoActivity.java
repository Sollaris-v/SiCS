package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sics.R;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.helper.DateCustom;
import com.example.sics.model.Chamado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class AbrirChamadoActivity extends AppCompatActivity {

    private EditText  campoTitulo, campoDescricao;
    private Spinner campoTipo;
    private Button botaoEnviar, botaoVoltar;
    private Chamado chamado;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abrir_chamado);

        campoTitulo = findViewById(R.id.editTitulo);
        campoTipo = findViewById(R.id.spinnerTipo);
        campoDescricao = findViewById(R.id.editDescricao);

        botaoEnviar = findViewById(R.id.botaoEnviar);
        //botaoVoltar = findViewById(R.id.botaoVoltar);

        carregarDadosSpinner();

/*
        //MUDA FONTE DO TITULO
        textView = findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), "AntonioRegular.ttf");
        textView.setTypeface(font);
 */

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoTitulo = campoTitulo.getText().toString();
                String textoTipo = campoTitulo.getText().toString();
                String textoDescricao = campoDescricao.getText().toString();

                //VERIFICA SE HA CAMPOS VAZIOS

                if (!textoTitulo.isEmpty()) {
                    if (!textoTipo.isEmpty()) {
                        if (!textoDescricao.isEmpty()) {

                            String dataAtual = DateCustom.dataAtual();

                            String mesAtual = DateCustom.mesAtual();

                            FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                            String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

                            //  GERAR ID CHAMADO
                            String id = String.valueOf(referencia.child("chamado").push());
                            id = id.replace("https", "");
                            id = id.replace("firebaseio", "");
                            id = id.replace("/", "");
                            id = id.replace(":", "");
                            id = id.replace(".", "");
                            id = id.replace("com", "");
                            id = id.replace("chamado", "");
                            id = id.replace("sics", "");
                            id = id.replace("7c8a1", "");
                            String codId = Base64Custom.codificarBase64(id);

                            chamado = new Chamado();
                            chamado.setTitulo(campoTitulo.getText().toString().toLowerCase());
                            chamado.setTipo(campoTipo.getSelectedItem().toString().toLowerCase());
                            chamado.setDescricaoCli(campoDescricao.getText().toString().toLowerCase());
                            chamado.setDataAbertura(dataAtual);
                            chamado.setMesAbertura(mesAtual);
                            chamado.setEstado("aberto");
                            chamado.setIdCliente(idUsuario);
                            chamado.setIdChamado(codId);
                            chamado.setIdFuncionario("---");
                            chamado.setDataFechamento("---");
                            chamado.setDescricaoFunc("---");
                            chamado.setEstado_tipo("aberto_"+campoTipo.getSelectedItem().toString().toLowerCase());
                            chamado.setEstado_idcli("aberto_"+idUsuario);
                            chamado.setSastifacao("0");

                            chamado.salvar();

                            Toast.makeText(AbrirChamadoActivity.this, "Chamado enviado!", Toast.LENGTH_SHORT).show();

                            finish();


                                        /*
                                        chamado = new Chamado();
                                        chamado.setTitulo(textoTitulo);
                                        chamado.setTipo(textoTipo);
                                        chamado.setDescricaoCli(textoDescricao);
                                        
                                         */

                                        //cadastrarChamado();

                        } else {
                            Toast.makeText(AbrirChamadoActivity.this, "Preencha a descrição!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AbrirChamadoActivity.this, "Escolha o tipo do problema!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AbrirChamadoActivity.this, "Preencha o título!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
    public void cadastrarChamado(View view){
        
        chamado = new Chamado();
        chamado.setTitulo(campoTitulo.getText().toString());
        chamado.setTipo(campoTipo.getSelectedItem().toString());
        chamado.setDescricaoCli(campoDescricao.getText().toString());

        chamado.salvar();

        Toast.makeText(AbrirChamadoActivity.this, "Chamado enviado!", Toast.LENGTH_SHORT).show();
        finish();
        
    }
 */

    private void carregarDadosSpinner(){

        String[] tipos = getResources().getStringArray(R.array.tipos);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoTipo.setAdapter(adapter);

    }

    public void btVoltar (View view){
        finish();
    }


    

    //Spinner spinner = (Spinner) findViewById(R.id.spinnerTipo);



}
