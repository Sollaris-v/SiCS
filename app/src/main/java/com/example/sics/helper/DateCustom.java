package com.example.sics.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String mesAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        String mesString = simpleDateFormat.format(data);
        return mesString;
    }
}
