package com.example.sics.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sics.R;
import com.example.sics.activity.fragments.BuscaFragment;
import com.example.sics.activity.fragments.EmAndamentoFragment;
import com.example.sics.activity.fragments.FechadosFragment;
import com.example.sics.activity.fragments.HomeFragment;
import com.example.sics.activity.ui.main.SectionsPagerAdapter;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, EmAndamentoFragment.OnFragmentInteractionListener,FechadosFragment.OnFragmentInteractionListener, BuscaFragment.OnFragmentInteractionListener {

    //private RecyclerView recyclerView;
    private FirebaseAuth autenticacao;
    private static FirebaseAuth autenticacao2;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    public String idUsuario, nomeUsuario, nivelUsuario, especialidadeUsuario, tipoChamado;
    private TextView campoNome;
    private ImageView cadastrarFunc, listaUsu, relatorio, busca;
    public static String emailUsu;
    private SearchView searchViewPesquisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);


        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();
            }
        });
*/
        //  RecyclerView
        //recyclerView = findViewById(R.id.recyclerView);

        campoNome = findViewById(R.id.textBemVindo);
        cadastrarFunc = findViewById(R.id.addFunc);
        listaUsu = findViewById(R.id.listarUsuarios);
        relatorio = findViewById(R.id.imagRelatorio);
        busca = findViewById(R.id.imagBusca);

        searchViewPesquisa = findViewById(R.id.searchViewPesquisa);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(emailUsuario);

        recuperarDadosCli();
       // bloquearCampos();
        verificaUsuInativo();



    }

    public void btDeslogar (View view){
        Toast.makeText(MenuActivity.this, "Usuário deslogado!", Toast.LENGTH_SHORT).show();
        deslogar();
    }

    public void deslogar(){
        try{
            autenticacao.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void btCadastrarFunc (View view){
        startActivity(new Intent(this, CadastroFuncActivity.class));
    }

    public void btListarUsuarios (View view){
        startActivity(new Intent(this, ListaUsuActivity.class));
    }


    public void btAbrirChamado (View view){
        startActivity(new Intent(this, AbrirChamadoActivity.class));
    }

    public void btVerPerfil (View view){
        startActivity(new Intent(this, PerfilActivity.class));
    }

    public void btVerRelatorio (View view){
        startActivity(new Intent(this, RelatorioActivity.class));
    }

    public void btBusca (View view){
        startActivity(new Intent(this, BuscaActivity.class));
    }

    public void btManual (View view) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String emailUsuario = currentUser.getEmail();
        emailUsuario = Base64Custom.codificarBase64(emailUsuario);

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String nivel = usuario.getNivel();

                if (nivel.equals("cliente")){

                    String url = "https://drive.google.com/file/d/1icTBNPN5wKpgqBjH2-uc5DiJOLNvhcx8/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }else if (nivel.equals("tecnico")){

                    String url = "https://drive.google.com/file/d/12XuYhxvzcWLB9bOnZMexwLeaYyginCNo/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }else if (nivel.equals("administrador")){

                    String url = "https://drive.google.com/file/d/1ESAHbFgShY6Q93FEyIAbAQJRD2G3jKO9/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }else if (nivel.equals("supervisor")){

                    String url = "https://drive.google.com/file/d/1bR-GixO_1XS8Cd-XKwWP2DZzLAglEr6S/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }else {

                    String url = "https://drive.google.com/file/d/1icTBNPN5wKpgqBjH2-uc5DiJOLNvhcx8/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void recuperarDadosCli() {
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                //Log.i("FIREBASE", dataSnapshot.getValue().toString() );
                nomeUsuario = usuario.getNome();
                nivelUsuario = usuario.getNivel();
                //Toast.makeText(MenuActivity.this, nomeUsuario, Toast.LENGTH_SHORT).show();
                campoNome.setText("Bem vindo " + nomeUsuario +"!");


                // BLOQUEAR INTERFACE DEPENDENDO DO NIVEL
                if (nivelUsuario.equals("cliente")){
                    cadastrarFunc.setVisibility(View.INVISIBLE);
                    listaUsu.setVisibility(View.INVISIBLE);
                    relatorio.setVisibility(View.INVISIBLE);
                    busca.setVisibility(View.INVISIBLE);
                } else if (nivelUsuario.equals("tecnico")){
                    cadastrarFunc.setVisibility(View.INVISIBLE);
                    listaUsu.setVisibility(View.INVISIBLE);
                    relatorio.setVisibility(View.INVISIBLE);
                } else if (nivelUsuario.equals("supervisor")){

                } else if (nivelUsuario.equals("administrador")){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String idUsu(){
        emailUsu  = autenticacao2.getCurrentUser().getEmail();
        emailUsu = Base64Custom.codificarBase64(emailUsu);
        return(emailUsu);

    }

    public void bloquearCampos (){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String emailUsuario = currentUser.getEmail();
        emailUsuario = Base64Custom.codificarBase64(emailUsuario);

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String nivel = usuario.getNivel();

                if (nivel.equals("cliente")){
                    searchViewPesquisa.setEnabled(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void verificaUsuInativo(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String emailUsuario = currentUser.getEmail();
        emailUsuario = Base64Custom.codificarBase64(emailUsuario);

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String situacao = usuario.getSituacao();

                if (situacao.equals("inativo")){
                    Toast.makeText(MenuActivity.this, "Sua conta está inativa!", Toast.LENGTH_SHORT).show();
                    deslogar();
                    Toast.makeText(MenuActivity.this, "Sua conta está inativa!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}