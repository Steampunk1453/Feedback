package com.feedback.football.utils;

import android.content.Context;

import com.feedback.football.R;

/**
 * Created by USER on 01/04/2017.
 */

public class Utils {

    /**
     * Método que devuelve el string de la licencia introduciendo el id (posicion en el array de licencias).
     *
     * @param context
     * @param tipo
     * @return
     */
    public static String getStringMinutoId(Context context, int tipo) {
        return context.getResources().getStringArray(R.array.minutos)[tipo];
    }

    public static String getStringJugadaId(Context context, int tipo) {
        return context.getResources().getStringArray(R.array.jugadas)[tipo];
    }
}
