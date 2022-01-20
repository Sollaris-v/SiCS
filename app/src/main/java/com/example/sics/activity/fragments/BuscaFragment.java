package com.example.sics.activity.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.ChamadoActivity;
import com.example.sics.activity.adapter.PesquisaAdapter;
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
 * {@link BuscaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuscaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuscaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String nivelUsuario;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerResultado;

    private List<Chamado> listaChamados;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private PesquisaAdapter pesquisaAdapter;
    private Spinner campoFiltro;
    private String itemSelecionado, idUsuario, idUsu;
    private BuscaFragment buscaFragment;

    public BuscaFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuscaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuscaFragment newInstance(String param1, String param2) {
        BuscaFragment fragment = new BuscaFragment();
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
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_busca, container, false);

       searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
       recyclerResultado = view.findViewById(R.id.recyclerResultado);

       listaChamados = new ArrayList<>();
        referencia = ConfiguracaoFirebase.getFirebaseDatabase().child("chamados");


        //CONFIGURAÇÂO RECYCLER
       recyclerResultado.setHasFixedSize(true);
        recyclerResultado.setLayoutManager(new LinearLayoutManager(getActivity()));

       pesquisaAdapter = new PesquisaAdapter(listaChamados, getActivity());
       recyclerResultado.setAdapter(pesquisaAdapter);
      // recyclerResultado.setAdapter(PesquisaAdapter);

        campoFiltro = view.findViewById(R.id.spinner);
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
                        getActivity(),
                        recyclerResultado,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Chamado chamadoSelecionado = listaChamados.get(position);

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
        //bloquearCampos();
        //recuperarDadosCli();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, filtros);
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
