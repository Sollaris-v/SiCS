package com.example.sics.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.activity.adapter.AbertosAdapter;
import com.example.sics.activity.adapter.FechadosAdapter;
import com.example.sics.config.ConfiguracaoFirebase;
import com.example.sics.helper.DateCustom;
import com.example.sics.helper.JavaMailAPI;
import com.example.sics.model.Chamado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RelatorioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Chamado> listaChamadosAb = new ArrayList<>();
    private List<Chamado> listaChamadosFe = new ArrayList<>();
    private List<Chamado> listaChamadosProbCel = new ArrayList<>();
    private List<Chamado> listaChamadosProbCom = new ArrayList<>();
    private List<Chamado> listaChamadosProbAp = new ArrayList<>();
    private List<Chamado> listaChamadosAbMes = new ArrayList<>();
    private List<Chamado> listaChamadosFeMes = new ArrayList<>();
    private List<Chamado> listaChamadosEmEsp = new ArrayList<>();
    //private List<Chamado> listaChamadosAb = new ArrayList<>();
    private AbertosAdapter adapterAb, adapterProbcel, adapterProbCom, adapterProbAp, adapterAbMes;
    private FechadosAdapter adapterFe, adapterFeMes, adapterEsp;
    //private DatabaseReference chamadosRef;
    private Query buscarChamadosAb, buscarChamadosFe, buscarChamadosProbCel, buscarChamadosProbCom, buscarChamadosProbAp, buscarChamadosAbMes, buscarChamadosFeMes, buscarChamadosEmEsp;
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
    private TextView totChamAb, totChamFe, tecDest, probCel, probCom, tecBaix, probAp, abertoMes, fechadoMes, totChamEsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);


        totChamAb = findViewById(R.id.textTotalChamAb);
        totChamFe = findViewById(R.id.textTotalChamFe);
        probCel = findViewById(R.id.textProbCel);
        probCom = findViewById(R.id.textProbCom);
        probAp = findViewById(R.id.textProbAp);
        abertoMes = findViewById(R.id.textAbMes);
        fechadoMes = findViewById(R.id.textFeMes);
        totChamEsp =  findViewById(R.id.textTotalChamEsp);

        //carregarDadosSpinnerFiltro();

        String mesAtual = DateCustom.mesAtual();

        DatabaseReference chamados = referencia.child("chamados");


        buscarChamadosAb = chamados.orderByChild("estado").equalTo("aberto");

        buscarChamadosFe = chamados.orderByChild("estado").equalTo("fechado");

        buscarChamadosEmEsp = chamados.orderByChild("estado").equalTo("em andamento");

        buscarChamadosProbCel = chamados.orderByChild("tipo").equalTo("problema com o celular");

        buscarChamadosProbCom = chamados.orderByChild("tipo").equalTo("problema com computador");

        buscarChamadosProbAp = chamados.orderByChild("tipo").equalTo("problema com o aplicativo");

        buscarChamadosAbMes = chamados.orderByChild("mesAbertura").equalTo(mesAtual);

        buscarChamadosFeMes = chamados.orderByChild("mesFechamento").equalTo(mesAtual);



        //Configurar adapter
        //adapter = new UsuariosAdapter(listaUsuarios, new ListaUsuActivity());
        adapterAb = new AbertosAdapter(listaChamadosAb, this);
        adapterFe = new FechadosAdapter(listaChamadosFe, this);
        adapterProbcel = new AbertosAdapter(listaChamadosProbCel, this);
        adapterProbCom = new AbertosAdapter(listaChamadosProbCom, this);
        adapterProbAp = new AbertosAdapter(listaChamadosProbAp, this);
        adapterAbMes = new AbertosAdapter(listaChamadosAbMes, this);
        adapterFeMes = new FechadosAdapter(listaChamadosFeMes, this);
        adapterEsp = new FechadosAdapter(listaChamadosEmEsp, this);

        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(new ListaUsuActivity());
        //recyclerView.setLayoutManager( layoutManager);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);



        //String dataAtual = DateCustom.mesAtual();
        //Toast.makeText(RelatorioActivity.this, dataAtual, Toast.LENGTH_SHORT).show();


    }



    public void onStart() {

        super.onStart();



        recuperarChamadosAb();
        recuperarChamadosFe();
        recuperarChamadosProbAp();
        recuperarChamadosProbCel();
        recuperarChamadosProbCom();
        recuperarChamadosAbMes();
        recuperarChamadosFeMes();
        recuperarChamadosEmEsp();
        //recuperarDadosCli();
    }

    public void onStop(){
        super.onStop();
        //chamadosRef.removeEventListener(valueEventListenerAbertos);
        buscarChamadosAb.removeEventListener(valueEventListenerAbertos);
    }

    public void recuperarChamadosAb(){

        valueEventListenerAbertos = buscarChamadosAb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosAb.add(chamado);
                }
                adapterAb.notifyDataSetChanged();



                int contador = listaChamadosAb.size();
                String cont = String.valueOf(contador);
                totChamAb.setText(cont);
                //Toast.makeText(RelatorioActivity.this, cont, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void recuperarChamadosFe(){

        valueEventListenerAbertos = buscarChamadosFe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosFe.add(chamado);
                }
                adapterFe.notifyDataSetChanged();



                int contador = listaChamadosFe.size();
                String cont = String.valueOf(contador);
                totChamFe.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosProbCel(){

        valueEventListenerAbertos = buscarChamadosProbCel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosProbCel.add(chamado);
                }
                adapterProbcel.notifyDataSetChanged();



                int contador = listaChamadosProbCel.size();
                String cont = String.valueOf(contador);
                probCel.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosProbCom(){

        valueEventListenerAbertos = buscarChamadosProbCom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosProbCom.add(chamado);
                }
                adapterProbCom.notifyDataSetChanged();



                int contador = listaChamadosProbCom.size();
                String cont = String.valueOf(contador);
                probCom.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosProbAp(){

        valueEventListenerAbertos = buscarChamadosProbAp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosProbAp.add(chamado);
                }
                adapterProbAp.notifyDataSetChanged();



                int contador = listaChamadosProbAp.size();
                String cont = String.valueOf(contador);
                probAp.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosAbMes(){

        valueEventListenerAbertos = buscarChamadosAbMes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosAbMes.add(chamado);
                }
                adapterAbMes.notifyDataSetChanged();



                int contador = listaChamadosAbMes.size();
                String cont = String.valueOf(contador);
                abertoMes.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosFeMes(){

        valueEventListenerAbertos = buscarChamadosFeMes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosFeMes.add(chamado);
                }
                adapterFeMes.notifyDataSetChanged();



                int contador = listaChamadosFeMes.size();
                String cont = String.valueOf(contador);
                fechadoMes.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarChamadosEmEsp(){

        valueEventListenerAbertos = buscarChamadosEmEsp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Chamado chamado = dados.getValue(Chamado.class);
                    listaChamadosEmEsp.add(chamado);
                }
                adapterFe.notifyDataSetChanged();



                int contador = listaChamadosEmEsp.size();
                String cont = String.valueOf(contador);
                totChamEsp.setText(cont);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void btEnviarEmail(View view){

        String emailUsuario  = autenticacao.getCurrentUser().getEmail();

        // ENVIAR EMAIL
        String mail = emailUsuario;
        String message = " Total de chamados abertos: "+ totChamAb.getText()+"\n Total de chamados fechados: "+totChamFe.getText()+"\n Total de chamados sobre problemas com o celular: "+probCel.getText()+
                "\n Total de chamados sobre problemas com computador: "+probCom.getText()+"\n Total de chamados sobre problema com aplicativo: "+probAp.getText()+
                "\n Total de chamados abertos esse mês: "+abertoMes.getText()+
                "\n Total de chamados fechados esse mês: "+fechadoMes.getText();
        String subject = "Atividades do SiCS";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();

        Toast.makeText(RelatorioActivity.this, "Atividades do aplicativo enviadas para o seu email!", Toast.LENGTH_LONG).show();
        finish();
    }
}