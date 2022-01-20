package com.example.sics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.adapter.PesquisaAdapter;
import com.example.sics.activity.fragments.BuscaFragment;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.Base64Custom;
import com.example.sics.helper.RecyclerItemClickListener;
import com.example.sics.model.Chamado;
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

public class BuscaActivity extends AppCompatActivity {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String nivelUsuario;

    private BuscaFragment.OnFragmentInteractionListener mListener;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerResultado;

    private List<Chamado> listaChamados;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private PesquisaAdapter pesquisaAdapter;
    private Spinner campoFiltro;
    private String itemSelecionado, idUsuario, idUsu;
    private BuscaFragment buscaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);


        searchViewPesquisa = findViewById(R.id.searchViewPesquisa);
        recyclerResultado = findViewById(R.id.recyclerResultado);

        listaChamados = new ArrayList<>();
        referencia = ConfiguracaoFirebase.getFirebaseDatabase().child("chamados");


        //CONFIGURAÇÂO RECYCLER
        pesquisaAdapter = new PesquisaAdapter(listaChamados, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(new ListaUsuActivity());
        recyclerResultado.setHasFixedSize(true);
        recyclerResultado.setLayoutManager(layoutManager);


        recyclerResultado.setAdapter(pesquisaAdapter);
        // recyclerResultado.setAdapter(PesquisaAdapter);

        campoFiltro = findViewById(R.id.spinner);
        carregarDadosSpinnerFiltro();

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(emailUsuario);

        //getUser();


        //Configurar searchView
        searchViewPesquisa.setQueryHint("Buscar chamados");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String textoDigitado = newText.toLowerCase();
                pesquisarChamados(textoDigitado);

                return true;
            }
        });

        // CONFIGURA O RECYCLERVIEW PARA SER CLICAVEL

        recyclerResultado.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        BuscaActivity.this,
                        recyclerResultado,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Chamado chamadoSelecionado = listaChamados.get(position);

                                Intent i = new Intent(BuscaActivity.this, ChamadoActivity.class);
                                i.putExtra("telaChamado", chamadoSelecionado);
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


    private void pesquisarChamados(String texto){

        listaChamados.clear();
        itemSelecionado = campoFiltro.getSelectedItem().toString();


        if (itemSelecionado.equals("Título")) {

            if (texto.length() > 0) {
                Query query = referencia.orderByChild("titulo").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaChamados.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaChamados.add(ds.getValue(Chamado.class));
                        }

                        pesquisaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        if (itemSelecionado.equals("Tipo")) {
            if (texto.length() > 0) {
                Query query = referencia.orderByChild("tipo").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaChamados.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaChamados.add(ds.getValue(Chamado.class));
                        }

                        pesquisaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
        if (itemSelecionado.equals("Data Abertura")) {
            if (texto.length() > 0) {
                Query query = referencia.orderByChild("dataAbertura").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaChamados.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaChamados.add(ds.getValue(Chamado.class));
                        }

                        pesquisaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        if (itemSelecionado.equals("Data Fechamento")) {
            if (texto.length() > 0) {
                Query query = referencia.orderByChild("dataFechamento").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaChamados.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaChamados.add(ds.getValue(Chamado.class));
                        }

                        pesquisaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
        if (itemSelecionado.equals("Estado")) {
            if (texto.length() > 0) {
                Query query = referencia.orderByChild("estado").startAt(texto).endAt(texto + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listaChamados.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            listaChamados.add(ds.getValue(Chamado.class));
                        }

                        pesquisaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }


    }

    private void carregarDadosSpinnerFiltro(){

        String[] filtros = getResources().getStringArray(R.array.filtro);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filtros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoFiltro.setAdapter(adapter);

    }


    public void btAbrirChamado (View view){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    public void bloquearCampos (){


        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);


                try{
                    nivelUsuario = (usuario.getNivel());
                }catch (Exception e){
                    e.printStackTrace();
                }


                if (nivelUsuario.equals("cliente")){
                    searchViewPesquisa.setEnabled(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarDadosCli() {

        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                nivelUsuario = (usuario.getNivel());

                // RECUPERAR ID USUARIO LOGADO
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                String emailUsuario  = autenticacao.getCurrentUser().getEmail();
                idUsuario = Base64Custom.codificarBase64(emailUsuario);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
