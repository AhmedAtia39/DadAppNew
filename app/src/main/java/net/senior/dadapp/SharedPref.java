package net.senior.dadapp;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class SharedPref {

    Context context;

    public SharedPref(Context context) {
        this.context = context;
    }


    ///////////////////////////////////////////user ///////////////////////////////////////////////
    public String getUri() {
        SharedPreferences sharedPref = context.getSharedPreferences("Uri", MODE_PRIVATE);
        return sharedPref.getString("Uri", null);
    }

    public void setUri(String Uri) {
        SharedPreferences sharedPref = context.getSharedPreferences("Uri", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString("Uri", Uri);
        editor.apply();
    }


}