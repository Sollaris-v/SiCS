package com.example.sics.activity.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.sics.R;
import com.example.sics.activity.MenuActivity;
import com.example.sics.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_4, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private static DatabaseReference referencia;
    private FirebaseAuth autenticacao;
    private static String nivel;
    int x;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        //verificarCli();
        //nivel = "tecnico";
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        x = 3;
        /*
        if (nivel.equals("cliente")){
            x = 3;
        }else{
            x = 4;
        }

         */
        return x;
    }


    public static void verificarCli (){

        String emailUsuario = MenuActivity.idUsu();

        // RECUPERAR INFORMAÇÕES DO USUARIO LOGADO
        DatabaseReference usuarioRef = referencia.child("usuarios").child(emailUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String tipo = usuario.getNivel();

                salvarNivel(tipo);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void salvarNivel(String ni){
        nivel = ni;
    }


}