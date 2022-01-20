package com.example.sics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.adapter.UsuariosAdapter;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.helper.RecyclerItemClickListener;
import com.example.sics.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private UsuariosAdapter adapter;
    //private DatabaseReference chamadosRef;
    private Query buscarUsuarios;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference referencia2;
    private ValueEventListener valueEventListenerAbertos;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private String idUsuario, nivelUsuario, especialidadeUsuario, tipoChamado;
    private TextView campoTipo, campoTeste;
    private Spinner campoFiltro;
    private SearchView searchViewPesquisa;
    private int i;
    private String itemSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usu);


        recyclerView = findViewById(R.id.recyclerUsuarios);
        searchViewPesquisa = findViewById(R.id.searchView);
        //campoFiltro = findViewById(R.id.spinnerUsuario);

        //carregarDadosSpinnerFiltro();


        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(emailUsuario);

        referencia2 = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nome");

        //Configurar adapter
        //adapter = new UsuariosAdapter(listaUsuarios, new ListaUsuActivity());
        adapter = new UsuariosAdapter(listaUsuarios, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(new ListaUsuActivity());
        recyclerView.setLayoutManager( layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        //Configurar searchView
        searchViewPesquisa.setQueryHint("Buscar todos os usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String textoDigitado = newText;
                pesquisarUsuarios(textoDigitado);

                return true;
            }
        });




        // CONFIGURA O RECYCLERVIEW PARA SER CLICAVEL

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        ListaUsuActivity.this,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSelecionado = listaUsuarios.get(position);

                                Intent i = new Intent(ListaUsuActivity.this, PerfilActivity.class);
                                i.putExtra("telaUsuario", usuarioSelecionado);
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }


                        }
                )
        );





    }



    public void onStart() {

        super.onStart();

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        recuperarUsuarios();
        //recuperarDadosCli();
    }

    public void onStop(){
        super.onStop();
        //chamadosRef.removeEventListener(valueEventListenerAbertos);
        buscarUsuarios.removeEventListener(valueEventListenerAbertos);
    }

    public void recuperarUsuarios(){

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UsuariosAdapter.clearData();

                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();

                //int contador = listaUsuarios.size();
                //String cont = String.valueOf(contador);
                //Toast.makeText(ListaUsuActivity.this, cont, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//RECUPERAR DADOS CLI
/*
    public void recuperarDadosCli() {

        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                nivelUsuario = (usuario.getNivel());
                especialidadeUsuario = (usuario.getEspecialidade());



                // FILTRAR POR NIVEL DO USUARIO

                if (nivelUsuario.equals("administrador")){

                    //RECUPERA CHAMADO
                    DatabaseReference chamado = referencia.child("chamados");
                    chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

                    adapter = new AbertosAdapter(listaUsuarios, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    AbertosAdapter.clearData();
                    recuperarUsuarios();

                }else if (nivelUsuario.equals("tecnico")){

                    if (especialidadeUsuario.equals("tecnico de celular")){

                        tipoChamado = ("problema com o celular");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosAbertos = chamado.orderByChild("tipo").equalTo(tipoChamado);

                        adapter = new AbertosAdapter(listaUsuarios, new ListaUsuActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(new ListaUsuActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        AbertosAdapter.clearData();
                        recuperarUsuarios();

                    }else if (especialidadeUsuario.equals("tecnico de computador")){

                        tipoChamado = ("problema com o computador");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosAbertos = chamado.orderByChild("tipo").equalTo(tipoChamado);

                        adapter = new AbertosAdapter(listaUsuarios, new ListaUsuActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        AbertosAdapter.clearData();
                        recuperarChamadosAbertos();

                    }else{

                    }


                }else if (nivelUsuario.equals("supervisor")){

                    //RECUPERA CHAMADO
                    DatabaseReference chamado = referencia.child("chamados");
                    chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

                    adapter = new AbertosAdapter(listaAbertos, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    AbertosAdapter.clearData();
                    recuperarChamadosAbertos();

                }else if (nivelUsuario.equals("cliente")){

                    //RECUPERA CHAMADO
                    DatabaseReference chamado = referencia.child("chamados");
                    chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

                    adapter = new AbertosAdapter(listaAbertos, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    AbertosAdapter.clearData();
                    recuperarChamadosAbertos();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/

//PESQUISAR USU

    private void pesquisarUsuarios(String texto){

        listaUsuarios.clear();
        //itemSelecionado = campoFiltro.getSelectedItem().toString();


        //if (itemSelecionado.equals("Todos")) {

            if (texto.length() > 0) {
                Query query = referencia2.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaUsuarios.add(ds.getValue(Usuario.class));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        //}
        /*
        if (itemSelecionado.equals("Clientes")) {
            if (texto.length() > 0) {
                Query query = referencia2.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");//.orderByChild("nivel").equalTo("cliente");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaUsuarios.add(ds.getValue(Usuario.class));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
        if (itemSelecionado.equals("Técnicos")) {
            if (texto.length() > 0) {
                Query query = referencia2.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");//.orderByChild("nivel").equalTo("tecnico");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaUsuarios.add(ds.getValue(Usuario.class));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        if (itemSelecionado.equals("Supervisores")) {
            if (texto.length() > 0) {
                Query query = referencia2.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");//.orderByChild("nivel").equalTo("supervisores");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaUsuarios.add(ds.getValue(Usuario.class));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
        if (itemSelecionado.equals("Administadores")) {
            if (texto.length() > 0) {
                Query query = referencia2.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");//.orderByChild("nivel").equalTo("administrador");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaUsuarios.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaUsuarios.add(ds.getValue(Usuario.class));
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
*/

    }

    public void btTodos (View view){

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nome");

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void btClientes (View view){

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nivel").equalTo("cliente");

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void btTecnicos (View view){

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nivel").equalTo("tecnico");

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void btAdministrador (View view){

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nivel").equalTo("administrador");

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void btSupervisor (View view){

        UsuariosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        DatabaseReference usuario = referencia.child("usuarios");
        buscarUsuarios = usuario.orderByChild("nivel").equalTo("supervisor");

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = buscarUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

/*
    private void carregarDadosSpinnerFiltro(){

        String[] filtros = getResources().getStringArray(R.array.listaDeUsuarios);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filtros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoFiltro.setAdapter(adapter);

    }

 */

}
