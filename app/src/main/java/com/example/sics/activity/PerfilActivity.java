package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sics.R;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PerfilActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha, campoDataNasc, campoCPF, campoTelRes, campoTelCel;
    private Spinner campoCargo, campoEspecialidade, campoSituacao;
    private Usuario usuarioSelecionado, usuarioN;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private String idUsuLogado, idUsuarioAtualizar, nivelUsu, emailUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        campoDataNasc = findViewById(R.id.editDatanasc);
        campoCPF = findViewById(R.id.editCPF);
        campoTelRes = findViewById(R.id.editTel);
        campoTelCel = findViewById(R.id.editCel);
        campoCargo = findViewById(R.id.spinnerCargo);
        campoEspecialidade = findViewById(R.id.spinnerTipo);
        campoSituacao = findViewById(R.id.spinnerSituacao);


        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        emailUsuario = autenticacao.getCurrentUser().getEmail();
        idUsuLogado = Base64Custom.codificarBase64(emailUsuario);

        // BLOQUEAR CAMPOS
        campoEmail.setEnabled(false);
        campoCPF.setEnabled(false);
        campoDataNasc.setEnabled(false);
        campoSenha.setEnabled(false);
        VerificaNiveleBloqueia();



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("telaUsuario");

            idUsuarioAtualizar = (usuarioSelecionado.getEmail());

            String especialidade = (usuarioSelecionado.getEspecialidade());
            String situacao = (usuarioSelecionado.getSituacao());
            String tipoUsu = (usuarioSelecionado.getNivel());

            //CARREGAR CAMPOS
            campoNome.setText(usuarioSelecionado.getNome());
            campoEmail.setText(usuarioSelecionado.getEmail());
            //campoSenha.setText(usuarioSelecionado.getSenha());
            campoDataNasc.setText(usuarioSelecionado.getDatanasc());
            campoCPF.setText(usuarioSelecionado.getCpf());
            campoTelCel.setText(usuarioSelecionado.getCelular());
            campoTelRes.setText(usuarioSelecionado.getTelefone());


            // CARREGAR DADOS SPINNER
            if (tipoUsu.equals("cliente")){
                carregarDadosSpinnerTipoUsuCli();

            } else if (tipoUsu.equals("tecnico")){
                carregarDadosSpinnerTipoUsuTec();

            } else if (tipoUsu.equals("supervisor")){
                carregarDadosSpinnerTipoUsuSup();
            } else if (tipoUsu.equals("administrador")){
                carregarDadosSpinnerTipoUsuAdm();
            } else {
                carregarDadosSpinnerTipoUsuCli();
            }

            // CARREGAR DADOS SPINNER SITUAÇÃO DO USUARIO
            if ( situacao.equals("inativo")){
                carregarDadosSpinnerSituacaoInt();
            } else {
                carregarDadosSpinnerSituacaoAtv();
            }

            // CARREGAR DADOS SPINNER ESPECIALIDADE USUARIO
            if (tipoUsu.equals("cliente")){

            }
            else{

                if (especialidade.equals("tecnico de celular")){
                    carregarDadosSpinnerEspecialidadeCel();
                } else if (especialidade.equals("tecnico de computador")){
                    carregarDadosSpinnerEspecialidadeCom();
                } else if (especialidade.equals("tecnico de celular e computador")){
                    carregarDadosSpinnerEspecialidadeCelCom();
                } else{ }
            }
        } else {
            carregarDadosUsuarioLogado();
        }


    }

    private void carregarDadosSpinnerTipoUsuCli() {

        String[] tipoUsuario = getResources().getStringArray(R.array.tipoUsuarioCli);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCargo.setAdapter(adapter);

    }

    private void carregarDadosSpinnerTipoUsuAdm() {

        String[] tipoUsuario = getResources().getStringArray(R.array.tipoUsuarioAdm);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCargo.setAdapter(adapter);

    }

    private void carregarDadosSpinnerTipoUsuTec() {

        String[] tipoUsuario = getResources().getStringArray(R.array.tipoUsuarioTec);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCargo.setAdapter(adapter);

    }

    private void carregarDadosSpinnerTipoUsuSup() {

        String[] tipoUsuario = getResources().getStringArray(R.array.tipoUsuarioSup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipoUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCargo.setAdapter(adapter);

    }

    private void carregarDadosSpinnerSituacaoAtv() {

        String[] situcao = getResources().getStringArray(R.array.situacaoAt);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, situcao);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoSituacao.setAdapter(adapter);

    }

    private void carregarDadosSpinnerSituacaoInt() {

        String[] situcao = getResources().getStringArray(R.array.situacaoIn);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, situcao);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoSituacao.setAdapter(adapter);

    }

    private void carregarDadosSpinnerEspecialidadeCel() {

        String[] especialidade = getResources().getStringArray(R.array.especialidadeCel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, especialidade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEspecialidade.setAdapter(adapter);

    }

    private void carregarDadosSpinnerEspecialidadeCom() {

        String[] especialidade = getResources().getStringArray(R.array.especialidadeCom);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, especialidade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEspecialidade.setAdapter(adapter);

    }

    private void carregarDadosSpinnerEspecialidadeCelCom() {

        String[] especialidade = getResources().getStringArray(R.array.especialidadeCelCom);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, especialidade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEspecialidade.setAdapter(adapter);

    }

    private void carregarDadosUsuarioLogado() {

        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                idUsuarioAtualizar = (usuario.getIdUsuario());
                String especialidade = (usuario.getEspecialidade());
                String situacao = (usuario.getSituacao());
                String tipoUsu = (usuario.getNivel());

                //CARREGAR CAMPOS
                campoNome.setText(usuario.getNome());
                campoEmail.setText(usuario.getEmail());
                //campoSenha.setText(usuario.getSenha());
                campoDataNasc.setText(usuario.getDatanasc());
                campoCPF.setText(usuario.getCpf());
                campoTelCel.setText(usuario.getCelular());
                campoTelRes.setText(usuario.getTelefone());

                // CARREGAR DADOS SPINNER
                if (tipoUsu.equals("cliente")){
                    carregarDadosSpinnerTipoUsuCli();

                } else if (tipoUsu.equals("tecnico")){
                    carregarDadosSpinnerTipoUsuTec();

                } else if (tipoUsu.equals("supervisor")){
                    carregarDadosSpinnerTipoUsuSup();
                } else if (tipoUsu.equals("administrador")){
                    carregarDadosSpinnerTipoUsuAdm();
                } else {
                    carregarDadosSpinnerTipoUsuCli();
                }

                // CARREGAR DADOS SPINNER SITUAÇÃO DO USUARIO
                if ( situacao.equals("inativo")){
                    carregarDadosSpinnerSituacaoInt();
                } else {
                    carregarDadosSpinnerSituacaoAtv();
                }

                // CARREGAR DADOS SPINNER ESPECIALIDADE USUARIO
                if (tipoUsu.equals("cliente")){

                }
                else {

                    if (especialidade.equals("tecnico de celular")) {
                        carregarDadosSpinnerEspecialidadeCel();
                    } else if (especialidade.equals("tecnico de computador")) {
                        carregarDadosSpinnerEspecialidadeCom();
                    } else if (especialidade.equals("tecnico de celular e computador")) {
                        carregarDadosSpinnerEspecialidadeCelCom();
                    } else {

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void btSalvar(View view) {

        // TRATA PARA SUPERVISOR NÃO CADASTRE ADM
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuLogado2 = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuLogado2);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarioN = dataSnapshot.getValue(Usuario.class);

                nivelUsu = usuarioN.getNivel();

                String nivelCadastrado = (campoCargo.getSelectedItem().toString().toLowerCase());

                String camEmail = String.valueOf(campoEmail.getText());
                if ((nivelUsu.equals("supervisor")) && (nivelCadastrado.equals("administrador")) ) {
                    Toast.makeText(PerfilActivity.this, "Um Supervisor não pode alterar um Administrador ou Supervisor!", Toast.LENGTH_SHORT).show();
                }else if ((nivelUsu.equals("supervisor")) && (nivelCadastrado.equals("supervisor")) && (emailUsuario != (camEmail))){
                    Toast.makeText(PerfilActivity.this, "Um Supervisor não pode alterar um Administrador ou Supervisor!", Toast.LENGTH_SHORT).show();
                }
                else{



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("telaUsuario");

            usuarioSelecionado.setNome(campoNome.getText().toString());
            usuarioSelecionado.setDatanasc(campoDataNasc.getText().toString());
            usuarioSelecionado.setTelefone(campoTelRes.getText().toString());
            usuarioSelecionado.setCelular(campoTelCel.getText().toString());
            usuarioSelecionado.setSituacao(campoSituacao.getSelectedItem().toString().toLowerCase());
            try {
                usuarioSelecionado.setEspecialidade(campoEspecialidade.getSelectedItem().toString().toLowerCase());
            }catch (Throwable e) {
                e.printStackTrace();
            }
            String cargoSelec = (campoCargo.getSelectedItem().toString());
            if (cargoSelec.equals("Técnico")) {
                usuarioSelecionado.setNivel("tecnico");
            } else {
                usuarioSelecionado.setNivel(campoCargo.getSelectedItem().toString().toLowerCase());
            }


            usuarioSelecionado.atualizar();
            Toast.makeText(PerfilActivity.this, "Atualizações salvas!", Toast.LENGTH_SHORT).show();
            finish();



        }else {

            DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuLogado);
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                    usuario.setNome(campoNome.getText().toString());
                    usuario.setDatanasc(campoDataNasc.getText().toString());
                    usuario.setTelefone(campoTelRes.getText().toString());
                    usuario.setCelular(campoTelCel.getText().toString());
                    usuario.setSituacao(campoSituacao.getSelectedItem().toString().toLowerCase());

                    String carg = (String)campoCargo.getSelectedItem();
                    if (carg.equals("Cliente")) {

                    }else{
                        usuario.setEspecialidade(campoEspecialidade.getSelectedItem().toString().toLowerCase());
                    }

            String cargoSelec = (campoCargo.getSelectedItem().toString());
            if (cargoSelec.equals("Técnico")) {
                usuario.setNivel("tecnico");
            } else {
                usuario.setNivel(campoCargo.getSelectedItem().toString().toLowerCase());
            }


            usuario.atualizar();
            finish();
            Toast.makeText(PerfilActivity.this, "Atualizações salvas!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void VerificaNiveleBloqueia(){

        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String tipoUsuario = usuario.getNivel();

                //BLOQUEIA ITENS PELO TIPO DO USUARIO
                if (tipoUsuario.equals("cliente")){
                    campoCargo.setEnabled(false);
                    campoEspecialidade.setEnabled(false);
                    campoSituacao.setEnabled(false);
                } else if (tipoUsuario.equals("tecnico")){
                    campoCargo.setEnabled(false);
                    campoEspecialidade.setEnabled(false);
                    campoSituacao.setEnabled(false);
                } else if (tipoUsuario.equals("supervisor")){
                } else if (tipoUsuario.equals("administrador")){
                } else {
                    carregarDadosSpinnerTipoUsuCli();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void resetPassword(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

            try {
            auth.sendPasswordResetEmail(emailUsuario)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PerfilActivity.this, "Instruções para resetar senha enviada para seu email!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
        }catch  (Throwable e) {
            e.printStackTrace();
        }



    }


}