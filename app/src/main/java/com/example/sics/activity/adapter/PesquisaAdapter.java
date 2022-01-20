package com.example.sics.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.model.Chamado;
import com.example.sics.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PesquisaAdapter extends RecyclerView.Adapter<PesquisaAdapter.MyViewHolder> {

      private static List<Chamado> busca;
      private Context context;

      private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
      public String idCli, nomeCli;

    public PesquisaAdapter(List<Chamado> lista, Context c) {
        this.busca =lista;
        this.context = c;
    }

    @NonNull
      @Override
      public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chamado, parent, false);
        return new MyViewHolder(itemLista);
      }

      @Override
      public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

          Chamado chamado = busca.get(position);

          idCli = String.valueOf(chamado.getIdCliente());
          recuperarDadosCli();



          holder.titulo.setText( chamado.getTitulo());
          holder.tipo.setText( chamado.getTipo());
          holder.data.setText(chamado.getDataAbertura());
          holder.autor.setText(nomeCli);
          //holder.autor.setText(chamado.getIdCliente());

      }

      @Override
      public int getItemCount() {
          return busca.size();
      }

      public class MyViewHolder extends RecyclerView.ViewHolder{

          TextView titulo, tipo, data, autor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            tipo = itemView.findViewById(R.id.textTipo);
            data = itemView.findViewById(R.id.textData);
            autor = itemView.findViewById(R.id.textAutor);
        }
    }

    public static void clearData() {
        //busca.clear(); // clear list
    }

    public void recuperarDadosCli() {
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idCli);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                //Log.i("FIREBASE", dataSnapshot.getValue().toString() );
                nomeCli = usuario.getNome();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
