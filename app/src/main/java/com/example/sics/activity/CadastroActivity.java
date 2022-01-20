package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.santalu.maskedittext.MaskEditText;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private MaskEditText campoTelefone, campoCelular, campoCPF, campoDatanasc;
    private Button botaoCadastrar, botaoVoltar;
    private FirebaseAuth autencicacao;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        campoTelefone = findViewById(R.id.editTel);
        campoCelular = findViewById(R.id.editCel);
        campoCPF = findViewById(R.id.editCPF);
        campoDatanasc = findViewById(R.id.editDatanasc);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        //botaoVoltar = findViewById(R.id.botaoVoltar);


        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();
                String textoTelefone = campoTelefone.getText().toString();
                String textoCelular = campoCelular.getText().toString();
                String textoCPF = campoCPF.getText().toString();
                String textoDatansc = campoDatanasc.getText().toString();

                //VERIFICA SE HA CAMPOS VAZIOS

                if (!textoNome.isEmpty()) {
                    if (!textoCPF.isEmpty()) {
                        if (!textoCelular.isEmpty()) {
                            if (!textoDatansc.isEmpty()) {
                                if (!textoEmail.isEmpty()) {
                                    if (!textoSenha.isEmpty()) {

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
                                            usuario.setNivel("cliente");
                                            usuario.setSituacao("ativo");

                                            cadastrarCliente();

                                        } else{
                                            Toast.makeText(CadastroActivity.this, "CPF digitado é inválido!", Toast.LENGTH_SHORT).show();
                                        }



                                    } else {
                                        Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CadastroActivity.this, "Preencha a data de nascimento!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha o celular!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha o CPF!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }

    public void btVoltar (View view){
        finish();
    }


}
