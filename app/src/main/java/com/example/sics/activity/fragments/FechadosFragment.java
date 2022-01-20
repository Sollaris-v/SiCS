package com.example.sics.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.ChamadoActivity;
import com.example.sics.activity.adapter.FechadosAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FechadosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FechadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FechadosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1, idUsuario, tipoChamado, nivelUsuario, especialidadeUsuario;
    private String mParam2;
    private RecyclerView recyclerView;
    private List<Chamado> listaFechados = new ArrayList<>();
    private Query chamadosFechados;
    private FechadosAdapter adapter;
    private DatabaseReference chamadosRef;
    private ValueEventListener valueEventListenerFechados;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener valueEventListenerAbertos;

    private OnFragmentInteractionListener mListener;

    public FechadosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FechadosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FechadosFragment newInstance(String param1, String param2) {
        FechadosFragment fragment = new FechadosFragment();
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

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        idUsuario = Base64Custom.codificarBase64(emailUsuario);



/*
        //   RECUPERA CHAMADOS ABERTOS PELO USUARIO
        String emailUsuario  = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        //chamadosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("chamados").child(idUsuario);
        DatabaseReference chamado = referencia.child("chamados");
        chamadosFechados = chamado.orderByChild("estado").equalTo("fechado");

 */




        //Configurar adapter
        adapter = new FechadosAdapter(listaFechados, getActivity());

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

                                Chamado chamadoSelecionado = listaFechados.get(position);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void onStart() {

        super.onStart();
        FechadosAdapter.clearData();  // IMPEDE DA LISTA SE REPETIR
        recuperarDadosCli();
    }

    public void onStop(){
        super.onStop();
        //chamadosRef.removeEventListener(valueEventListenerFechados);
        chamadosFechados.removeEventListener(valueEventListenerFechados);
    }

    public void recuperarChamadosFechados(){

        //valueEventListenerFechados = chamadosRef.addValueEventListener(new ValueEventListener() {
        valueEventListenerFechados = chamadosFechados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FechadosAdapter.clearData();

                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaFechados.add(chamado);
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
                    chamadosFechados = chamado.orderByChild("estado").equalTo("fechado");

                    adapter = new FechadosAdapter(listaFechados, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    FechadosAdapter.clearData();
                    recuperarChamadosFechados();

                }else if (nivelUsuario.equals("tecnico")){

                    if (especialidadeUsuario.equals("tecnico de celular")){

                        //tipoChamado = ("problema com o celular");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosFechados = chamado.orderByChild("estado_tipo").equalTo("fechado_problema com o celular");

                        adapter = new FechadosAdapter(listaFechados, getActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        FechadosAdapter.clearData();
                        recuperarChamadosFechados();

                    }else if (especialidadeUsuario.equals("tecnico de computador")){

                        //tipoChamado = ("problema com o computador");

                        //    Recuperar chamados por tipo

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosFechados = chamado.orderByChild("estado_tipo").equalTo("fechado_problema com computador");

                        adapter = new FechadosAdapter(listaFechados, getActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        FechadosAdapter.clearData();
                        recuperarChamadosFechados();

                    }else{

                        DatabaseReference chamado = referencia.child("chamados");
                        chamadosFechados = chamado.orderByChild("estado").equalTo("fechado");

                        adapter = new FechadosAdapter(listaFechados, getActivity());

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager( layoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        FechadosAdapter.clearData();
                        recuperarChamadosFechados();

                    }


                }else if (nivelUsuario.equals("supervisor")){

                    //RECUPERA CHAMADO
                    DatabaseReference chamado = referencia.child("chamados");
                    chamadosFechados = chamado.orderByChild("estado").equalTo("fechado");

                    adapter = new FechadosAdapter(listaFechados, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    FechadosAdapter.clearData();
                    recuperarChamadosFechados();

                }else if (nivelUsuario.equals("cliente")){

                    //RECUPERA CHAMADO
                    DatabaseReference chamado = referencia.child("chamados");
                    chamadosFechados = chamado.orderByChild("estado_idcli").equalTo("fechado_"+idUsuario);

                    adapter = new FechadosAdapter(listaFechados, getActivity());

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager( layoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    FechadosAdapter.clearData();
                    recuperarChamadosFechados();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
