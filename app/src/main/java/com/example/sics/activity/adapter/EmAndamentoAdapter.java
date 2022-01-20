package com.example.sics.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.helper.Base64Custom;
import com.example.sics.model.Chamado;
import com.example.sics.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmAndamentoAdapter extends RecyclerView.Adapter<EmAndamentoAdapter.MyViewHolder> {

    private static List<Chamado> fechados;
    private Context context;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    public String idCli, nomeCli;


    public EmAndamentoAdapter(List<Chamado> lista, Context c) {
        this.fechados = lista;
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

        Chamado chamado = fechados.get(position);

        idCli = String.valueOf(chamado.getIdCliente());
        String emailUsu = Base64Custom.decodificarBase64(idCli);

        holder.titulo.setText( chamado.getTitulo());
        holder.tipo.setText( chamado.getTipo());
        holder.data.setText(chamado.getDataAbertura());
        holder.autor.setText(emailUsu);
        //holder.data.setText(chamado.getIdCliente());



    }

    @Override
    public int getItemCount() {
        return fechados.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, tipo, data, autor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //ALTERA IDUSUARIO PARA NOME
            // DatabaseReference usuario = referencia.child("clientes").child(String.valueOf(autor)).child("nome");


            titulo = itemView.findViewById(R.id.textTitulo);
            tipo = itemView.findViewById(R.id.textTipo);
            data = itemView.findViewById(R.id.textData);
            autor = itemView.findViewById(R.id.textAutor);
        }
    }

    public static void clearData() {
        fechados.clear(); // clear list
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
