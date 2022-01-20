package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sics.R;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.helper.DateCustom;
import com.example.sics.helper.JavaMailAPI;
import com.example.sics.model.Chamado;
import com.example.sics.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ChamadoActivity extends AppCompatActivity {

    private TextView campoTitulo, campoAbertoPor, campoFechadoPor, campoAbertoEm, campoFechadoEm, campoTipo, campoContato1, campoContato2;
    private EditText campoDescriCli, campoDescriTec;
    private Spinner campoEstado;
    private RatingBar campoEstrelas;
    private Chamado chamadoSelecionado;
    private String idCli, idFunc, statusAntigo, idUsuario;
    private Chamado chamado;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Button btSalvar, btCancelar, btEstrela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamado);




        campoTitulo = findViewById(R.id.textTitulo);
        campoAbertoPor = findViewById(R.id.textAbertoPor);
        campoFechadoPor = findViewById(R.id.textFechadoPor);
        campoContato1 = findViewById(R.id.textTel1);
        campoContato2 = findViewById(R.id.textTel2);
        campoAbertoEm = findViewById(R.id.textAbertoEm);
        campoFechadoEm = findViewById(R.id.textFechadoEm);
        campoDescriCli = findViewById(R.id.editDescricaoCli);
        campoDescriTec = findViewById(R.id.editDescricaoTec);
        campoEstrelas = findViewById(R.id.ratingBar);
        campoTipo = findViewById(R.id.textTipo);
        campoEstado = findViewById(R.id.spinnerEstado);
        btSalvar = findViewById(R.id.buttonSalvar);
        btCancelar = findViewById(R.id.buttonCancelar);
        btEstrela = findViewById(R.id.button7);


        campoEstrelas.setEnabled(false);
        campoDescriCli.setEnabled(false);
        campoDescriTec.setEnabled(false);
        btCancelar.setVisibility(View.GONE);
        btEstrela.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            chamadoSelecionado = (Chamado) bundle.getSerializable("telaChamado");

            //RECUPERAR DADOS CLIENTE
            idCli = String.valueOf(chamadoSelecionado.getIdCliente());
            recuperarDadosCli();

            //RECUPERAR DADOS FUNCIONARIO
            idFunc = String.valueOf(chamadoSelecionado.getIdFuncionario());
            recuperarDadosFunc();

            //RECUPERAR O ESTADO
            String estado = String.valueOf(chamadoSelecionado.getEstado());
            statusAntigo = estado;
            if (estado.equals("aberto")){
                carregarDadosSpinnerAberto();
            }else if (estado.equals("fechado")){
                carregarDadosSpinnerFechado();
            }else if (estado.equals("em andamento")){
                carregarDadosSpinnerEmAndamento();
            }

            float stars = 0;
            try {
                 stars = Float.valueOf(chamadoSelecionado.getSastifacao());
            }catch (Exception e) {
            }

            campoTitulo.setText(chamadoSelecionado.getTitulo());
            campoTipo.setText(chamadoSelecionado.getTipo());
            campoAbertoEm.setText(chamadoSelecionado.getDataAbertura());
            campoFechadoEm.setText(chamadoSelecionado.getDataFechamento());
            campoDescriCli.setText(chamadoSelecionado.getDescricaoCli());
            campoDescriTec.setText(chamadoSelecionado.getDescricaoFunc());
            campoEstrelas.setRating(stars);



        }
        else{

        }

        travarCampos();
    }

    public void recuperarDadosCli() {
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idCli);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                campoAbertoPor.setText(usuario.getNome());
                campoContato1.setText(usuario.getCelular());
                campoContato2.setText(usuario.getTelefone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarDadosFunc() {
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idFunc);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                try {
                    String nomeFunc = (usuario.getNome());
                    campoFechadoPor.setText(nomeFunc);
                }catch (Exception e) {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void carregarDadosSpinnerAberto(){

        String[] estado = getResources().getStringArray(R.array.aberto);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estado);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

    }

    private void carregarDadosSpinnerFechado(){

        String[] estado = getResources().getStringArray(R.array.fechado);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estado);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

    }

    private void carregarDadosSpinnerEmAndamento(){

        String[] estado = getResources().getStringArray(R.array.andamento);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estado);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

    }

    public void btSalvar (View view){

        String dataAtual = DateCustom.dataAtual();
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String mesAtual = DateCustom.mesAtual();

        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        String textoCampo = campoDescriTec.getText().toString();

        if (!textoCampo.isEmpty()) {


            Bundle bundle = getIntent().getExtras();
            chamadoSelecionado = (Chamado) bundle.getSerializable("telaChamado");

            //float stars = 0;
            //stars = campoEstrelas.getRating();
            //String STARS = String.valueOf(stars);

            chamadoSelecionado.setEstado(campoEstado.getSelectedItem().toString().toLowerCase());
            chamadoSelecionado.setDescricaoFunc(campoDescriTec.getText().toString().toLowerCase());
            //chamadoSelecionado.setSastifacao(STARS);
            chamadoSelecionado.setIdFuncionario(idUsuario);
            chamadoSelecionado.setEstado_idcli(campoEstado.getSelectedItem().toString().toLowerCase()+"_"+idCli);
            chamadoSelecionado.setEstado_tipo(campoEstado.getSelectedItem().toString().toLowerCase()+"_"+chamadoSelecionado.getTipo());

            if (campoEstado.getSelectedItem().toString().toLowerCase().equals("fechado")){
                chamadoSelecionado.setDataFechamento(dataAtual);
                chamadoSelecionado.setMesFechamento(mesAtual);
            }else{

            }


            String statusNovo = campoEstado.getSelectedItem().toString().toLowerCase();
            if (statusAntigo.equals("aberto") && statusNovo.equals("fechado") || statusAntigo.equals("em andamento") && statusNovo.equals("fechado")) {
                // ENVIAR EMAIL
                String mail = Base64Custom.decodificarBase64(idCli);
                String message = "Gostariamos de lhe informar que seu chamado de titulo: "+ chamadoSelecionado.getTitulo()+", foi alterado para FECHADO, portanto concluído que o problema foi resolvido, " +
                        "caso esse não for o caso, abra um novo chamado. Muito obrigado por usar nosso aplicativo!";
                String subject = "Seu chamado foi fechado!";
                JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
                javaMailAPI.execute();
            }else{

            }


            chamadoSelecionado.atualizar();
            finish();

            Toast.makeText(ChamadoActivity.this, "Atualizações salvas!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(ChamadoActivity.this, "Preencha o campo de descrição do técnico!", Toast.LENGTH_SHORT).show();
        }


    }

    public void btApagar (View view){

        Bundle bundle = getIntent().getExtras();
        chamadoSelecionado = (Chamado) bundle.getSerializable("telaChamado");
        chamadoSelecionado.apagar();

        finish();

        Toast.makeText(ChamadoActivity.this, "Chamado apagado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public void btVoltar (View view){
        finish();
    }

    public void travarCampos() {

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        emailUsuario = Base64Custom.codificarBase64(emailUsuario);

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String email = usuario.getEmail();
                email = Base64Custom.codificarBase64(email);
                String nivel = usuario.getNivel();
                String estado = (String)campoEstado.getSelectedItem();





                // TRAVA CAMPOS PARA QUEM ABRIU O CHAMADO
                if (email.equals(idCli)){

                    if (nivel.equals("cliente") || nivel.equals("tecnico")){
                        campoEstado.setEnabled(false);
                        btSalvar.setEnabled(false);
                        btCancelar.setVisibility(View.VISIBLE);
                        btSalvar.setVisibility(View.GONE);
                        if (estado.equals("fechado")){
                            campoEstrelas.setEnabled(true);
                            btEstrela.setVisibility(View.VISIBLE);
                            btSalvar.setEnabled(true);
                            btCancelar.setVisibility(View.GONE);
                        }
                    }else{
                        btSalvar.setEnabled(true);
                        if (estado.equals("fechado")){
                            btEstrela.setVisibility(View.VISIBLE);
                            campoEstrelas.setEnabled(true);
                        }
                    }
                }
                 if (email.equals(idFunc) && estado.equals("em andamento")){
                     campoDescriTec.setEnabled(true);
                 }
                 if (nivel.equals("administrador") || nivel.equals("supervisor")){
                     if (estado.equals("fechado")){
                         btCancelar.setVisibility(View.GONE);
                     }else{
                         btCancelar.setVisibility(View.VISIBLE);
                     }

                 }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public void btCancelarChamado (View view){

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        emailUsuario = Base64Custom.codificarBase64(emailUsuario);

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        final DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String cargo = usuario.getNivel();
                String nome = usuario.getNome();


                Bundle bundle = getIntent().getExtras();
                chamadoSelecionado = (Chamado) bundle.getSerializable("telaChamado");

                chamadoSelecionado.setEstado("fechado");
                if (cargo.equals("cliente")){
                    chamadoSelecionado.setDescricaoFunc("O chamado foi cancelado pelo propio cliente.");
                }else if (cargo.equals("administrador"))
                {
                    chamadoSelecionado.setDescricaoFunc("O chamado foi cancelado pelo Administrador "+ nome +". Caso tenha alguma dúvida abra um novo chamado do tipo 'problema com o aplicativo.");
                }else if (cargo.equals("supervisor")){
                    chamadoSelecionado.setDescricaoFunc("O chamado foi cancelado pelo Supervisor "+ nome +". Caso tenha alguma dúvida abra um novo chamado do tipo 'problema com o aplicativo.");
                }
                chamadoSelecionado.setEstado_idcli("fechado_"+idCli);
                chamadoSelecionado.setEstado_tipo("fechado_"+chamadoSelecionado.getTipo());


                // ENVIAR EMAIL
                String mail = Base64Custom.decodificarBase64(idCli);
                String message = "Gostariamos de lhe informar que seu chamado de titulo: "+ chamadoSelecionado.getTitulo()+" foi cancelado!" +
                        " Muito obrigado por usar nosso aplicativo!";
                String subject = "Seu chamado '"+chamadoSelecionado.getTitulo()+"' foi cancelado!";
                //JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
                enviarEmail(mail,subject,message);


            chamadoSelecionado.atualizar();

            Toast.makeText(ChamadoActivity.this, "Chamado Cancelado!", Toast.LENGTH_SHORT).show();
            finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void enviarEmail(String mail, String subject, String message){
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();
    }

    public void stra (View view) {

        Bundle bundle = getIntent().getExtras();
        chamadoSelecionado = (Chamado) bundle.getSerializable("telaChamado");

        float stars = 0;
        stars = campoEstrelas.getRating();
        String STARS = String.valueOf(stars);

        chamadoSelecionado.setSastifacao(STARS);
        chamadoSelecionado.atualizar();

        Toast.makeText(ChamadoActivity.this, "Avaliação enviada!", Toast.LENGTH_SHORT).show();
        finish();

    }

}
