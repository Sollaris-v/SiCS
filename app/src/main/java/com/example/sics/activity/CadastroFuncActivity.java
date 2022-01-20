package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sics.R;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.helper.ValidaCPF;
import com.example.sics.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskedittext.MaskEditText;

public class CadastroFuncActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private MaskEditText campoTelefone, campoCelular, campoCPF, campoDatanasc;
    private Button botaoCadastrar, botaoVoltar;
    private Spinner campoTipo, campoCargo;
    private FirebaseAuth autencicacao;
    private Usuario usuario, usuarioN;
    private FirebaseAuth autenticacao;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private String nivelUsu, textoNome, textoEmail, textoSenha, textoTelefone, textoCelular, textoCPF, textoDatansc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_func);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        campoTelefone = findViewById(R.id.editTel);
        campoCelular = findViewById(R.id.editCel);
        campoCPF = findViewById(R.id.editCPF);
        campoDatanasc = findViewById(R.id.editDatanasc);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        //botaoVoltar = findViewById(R.id.botaoVoltar);
        campoTipo = findViewById(R.id.spinnerTipo);
        campoCargo = findViewById(R.id.spinnerCargo);

        carregarDadosSpinner();

        campoSenha.setEnabled(false);



        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textoNome = campoNome.getText().toString();
                textoEmail = campoEmail.getText().toString();
                textoSenha = campoSenha.getText().toString();
                textoTelefone = campoTelefone.getText().toString();
                textoCelular = campoCelular.getText().toString();
                textoCPF = campoCPF.getText().toString();
                textoDatansc = campoDatanasc.getText().toString();

                //VERIFICA SE HA CAMPOS VAZIOS

                if (!textoNome.isEmpty()) {
                    if (!textoCPF.isEmpty()) {
                        if (!textoCelular.isEmpty()) {
                            if (!textoDatansc.isEmpty()) {
                                if (!textoEmail.isEmpty()) {
                                    if (!textoSenha.isEmpty()) {

                                        // TRATA PARA SUPERVISOR NÃO CADASTRE ADM
                                        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                                        String emailUsuario = autenticacao.getCurrentUser().getEmail();
                                        String idUsuLogado = Base64Custom.codificarBase64(emailUsuario);
                                        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuLogado);
                                        usuarioRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                usuarioN = dataSnapshot.getValue(Usuario.class);

                                                    nivelUsu = usuarioN.getNivel();

                                                String nivelCadastrado = (campoCargo.getSelectedItem().toString());

                                                if ((nivelUsu.equals("supervisor") && nivelCadastrado.equals("administrador")) || (nivelUsu.equals("supervisor") && nivelCadastrado.equals("supervisor"))){
                                                    Toast.makeText(CadastroFuncActivity.this, "Um Supervisor não pode cadastrar um Administrador ou Supervisor!", Toast.LENGTH_SHORT).show();
                                                }else{

                                                    String CPF = textoCPF;
                                                    CPF = CPF.replace(".", "");
                                                    CPF = CPF.replace("-", "");

                                                    if (ValidaCPF.isCPF(CPF) == true){

                                                    usuario = new Usuario();
                                                    usuario.setNome(textoNome);
                                                    usuario.setEmail(textoEmail);
                                                    usuario.setSenha(textoSenha);
                                                    usuario.setTelefone(textoTelefone);
                                                    usuario.setCelular(textoCelular);
                                                    usuario.setCpf(textoCPF);
                                                    usuario.setDatanasc(textoDatansc);
                                                    usuario.setNivel(campoCargo.getSelectedItem().toString());
                                                    usuario.setEspecialidade(campoTipo.getSelectedItem().toString());
                                                    usuario.setSituacao("ativo");

                                                    cadastrarCliente();

                                                    } else{
                                                        Toast.makeText(CadastroFuncActivity.this, "CPF digitado é inválido!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
/*
                                        String nivelCadastrado = (campoCargo.getSelectedItem().toString());

                                        if (nivelUsu.equals("supervisor") && nivelCadastrado.equals("administrador")){
                                            Toast.makeText(CadastroFuncActivity.this, "Um Supervisor não pode cadastrar um Administrador!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(CadastroFuncActivity.this, "PASSOU AQUI", Toast.LENGTH_SHORT).show();
                                        usuario = new Usuario();
                                        usuario.setNome(textoNome);
                                        usuario.setEmail(textoEmail);
                                        usuario.setSenha(textoSenha);
                                        usuario.setTelefone(textoTelefone);
                                        usuario.setCelular(textoCelular);
                                        usuario.setCpf(textoCPF);
                                        usuario.setDatanasc(textoDatansc);
                                        usuario.setNivel(campoCargo.getSelectedItem().toString());
                                        usuario.setEspecialidade(campoTipo.getSelectedItem().toString());
                                        usuario.setSituacao("ativo");

                                        cadastrarCliente();
                                        }

 */

                                    } else {
                                        Toast.makeText(CadastroFuncActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(CadastroFuncActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CadastroFuncActivity.this, "Preencha a data de nascimento!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastroFuncActivity.this, "Preencha o celular!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroFuncActivity.this, "Preencha o CPF!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroFuncActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        public void cadastrarCliente(){

            autencicacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autencicacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setIdUsuario(idUsuario);
                        usuario.salvar();

                        Toast.makeText(CadastroFuncActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();

                    }else{

                        String excecao = "";
                        try{
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                            excecao = "Digite uma senha mais forte!";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "Digite um email válido!";
                        }catch (FirebaseAuthUserCollisionException e){
                            excecao = "Esta conta já foi cadastrada!";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(CadastroFuncActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }

    private void carregarDadosSpinner(){

        String[] especialidade = getResources().getStringArray(R.array.especialidadeCel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, especialidade);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoTipo.setAdapter(adapter);

        String[] cargos = getResources().getStringArray(R.array.cargos);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cargos);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCargo.setAdapter(adapter2);

    }

    public void btVoltar (View view){
        finish();
    }



}
