package com.example.sics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sics.R;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class LoginActivity extends IntroActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        addSlide(new SimpleSlide.Builder()

                .title("Titulo")
                .description("Descrição")
                .image(R.drawable.logo2)
                .background(android.R.color.holo_blue_light)
                .build()
        );

        addSlide(new SimpleSlide.Builder()
                .title("Titulo")
                .description("Descrição2")
                .image(R.drawable.logo2)
                .background(android.R.color.holo_blue_light)
                //.canGoForward(false)
                .build()

        );

        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmail( textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin();


                    } else {
                        Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void btCadastro (View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }


    public void abrirTelaHome(){
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }
    /*
    public void btLogin (View view){

        startActivity(new Intent(this, MenuActivity.class));

    }

     */

    public void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword( usuario.getEmail(), usuario.getSenha() ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful() ){

                    abrirTelaHome();

                }else {

                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Email e Senha não correspondem ao usuario cadastrado!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaHome();
        }
    }

    public void resetPassword(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String textoEmail = campoEmail.getText().toString();

        if(!textoEmail.isEmpty()){



        try {
            auth.sendPasswordResetEmail(textoEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Instruções para resetar senha enviada para seu email!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, "Email digitado é inválido!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch  (Throwable e) {
            e.printStackTrace();
        }

        }else {
            Toast.makeText(LoginActivity.this, "Instruções para resetar senha enviada para seu email!", Toast.LENGTH_SHORT).show();
        }

    }

}
