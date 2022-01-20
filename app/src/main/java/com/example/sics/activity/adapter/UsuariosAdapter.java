package com.example.sics.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sics.R;
import com.example.sics.model.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UsuariosAdapter  extends RecyclerView.Adapter<UsuariosAdapter.MyViewHolder> {

    private static List<Usuario> usuarios;
    private Context context;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    public String idCli, nomeCli, tipoChamado;


    public UsuariosAdapter(List<Usuario> lista, Context c) {
        this.usuarios = lista;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_usuario, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = usuarios.get(position);

        holder.titulo.setText(usuario.getNome());
        holder.tipo.setText(usuario.getEmail());
        holder.data.setText(usuario.getNivel());
        holder.autor.setText(usuario.getCelular());
        //holder.autor.setText(chamado.getIdCliente());

        /*

        if (tipoChamado.equals("aberto")) {

            holder.titulo.setText(chamado.getTitulo());
            holder.tipo.setText(chamado.getTipo());
            holder.data.setText(chamado.getDataAbertura());
            holder.autor.setText(nomeCli);
            //holder.autor.setText(chamado.getIdCliente());

        }else{
            //abertos.remove(position);

        }

         */


    }

    @Override
    public int getItemCount() {
        return usuarios.size();
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
        usuarios.clear(); // clear list
    }

    public static void clearItem() {

    }




}
