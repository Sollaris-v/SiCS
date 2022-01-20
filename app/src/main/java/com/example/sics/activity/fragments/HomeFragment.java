package com.example.sics.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.ChamadoActivity;
import com.example.sics.activity.adapter.AbertosAdapter;
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

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private List<Chamado> listaAbertos = new ArrayList<>();
    private AbertosAdapter adapter;
    //private DatabaseReference chamadosRef;
    private Query chamadosAbertos;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference referencia2 = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener valueEventListenerAbertos;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private String idUsuario, nivelUsuario, especialidadeUsuario, tipoChamado;
    private TextView campoTipo, campoTeste;
    private int i;


    private OnFragmentInteractionListener mListener;

    public HomeFragment() {}

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerAbertos);
        campoTipo = view.findViewById(R.id.textViewTipo);
        campoTeste = view.findViewById(R.id.textViewTeste);

         String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(emailUsuario);

        tipoChamado = campoTipo.getText().toString();

        DatabaseReference chamado = referencia.child("chamados");
        chamadosAbertos = chamado.orderByChild("estado").equalTo("erro");


        //chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

        //teste();

        // VERIFICA O NIVEL DO USUARIO

/*
        DatabaseReference chamado = referencia.child("chamados");
        //chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");
        chamadosAbertos = chamado.orderByChild("tipo").equalTo("problema com o celular");


        if (nivelUsuario.equals("administrador")){

            //RECUPERA CHAMADO
            DatabaseReference chamado = referencia.child("chamados");
            chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

        }else if (nivelUsuario.equals("tecnico")){

            //RECUPERA CHAMADO
            DatabaseReference chamado = referencia.child("chamados");
            chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto").orderByChild("tipo").equalTo(tipoChamado);


        }else if (nivelUsuario.equals("supervisor")){

            //RECUPERA CHAMADO
            DatabaseReference chamado = referencia.child("chamados");
            chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

        }else if (nivelUsuario.equals("cliente")){

            //RECUPERA CHAMADO
            DatabaseReference chamado = referencia.child("chamados");
            chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto").orderByChild("idCliente").equalTo(idUsuario);

        }





        DatabaseReference chamado = referencia.child("chamados");
        chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto").orderByChild("idCliente").equalTo(idUsuario);

        //   RECUPERA CHAMADOS

        //   RECUPERA CHAMADOS ABERTOS PELO USUARIO
        //String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        //String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        //chamadosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("chamados").child(idUsuario);
        //DatabaseReference chamado = referencia.child("chamados");
        //chamadosAbertos = chamado.orderByChild("estado").equalTo("aberto");

*/


        //Configurar adapter
        adapter = new AbertosAdapter(listaAbertos, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager( layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);




        // CONFIGURA O RECYCLERVIEW PARA SER CLICAVEL
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Chamado chamadoSelecionado = listaAbertos.get(position);

                                Intent i = new Intent(getActivity(), ChamadoActivity.class);
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


        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    public void onStart() {

        super.onStart();

        AbertosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR

        //recuperarChamadosAbertos();
        recuperarDadosCli();
    }

    public void onStop(){
        super.onStop();
        //chamadosRef.removeEventListener(valueEventListenerAbertos);
        chamadosAbertos.removeEventListener(valueEventListenerAbertos);
    }

    public void recuperarChamadosAbertos(){

        //valueEventListenerAbertos = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerAbertos = chamadosAbertos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AbertosAdapter.clearData();

                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaAbertos.add(chamado);
                }

                adapter.notifyDataSetChanged();
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
                especialidadeUsuario = (usuario.getEspecialidade());

                // RECUPERAR ID USUARIO LOGADO
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                String emailUsuario  = autenticacao.getCurrentUser().getEmail();
                idUsuario = Base64Custom.codificarBase64(emailUsuario);



                // FILTRAR POR NIVEL DO USUARIO

                if (nivelUsuario.equals("administrador")){

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

                }else if (nivelUsuario.equals("tecnico")){

                    if (especialidadeUsuario.equals("tecnico de celular")){

                        //tipoChamado = ("problema com o celular");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosAbertos = chamado.orderByChild("estado_tipo").equalTo("aberto_problema com o celular");

                        adapter = new AbertosAdapter(listaAbertos, getActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        AbertosAdapter.clearData();
                        recuperarChamadosAbertos();

                    }else if (especialidadeUsuario.equals("tecnico de computador")){

                        //tipoChamado = ("problema com o computador");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosAbertos = chamado.orderByChild("estado_tipo").equalTo("aberto_problema com computador");

                        adapter = new AbertosAdapter(listaAbertos, getActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        AbertosAdapter.clearData();
                        recuperarChamadosAbertos();

                    }else{

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
                    chamadosAbertos = chamado.orderByChild("estado_idcli").equalTo("aberto_"+idUsuario);

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



}
