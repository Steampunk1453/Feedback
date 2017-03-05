package com.feedback.football.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by folio on 04/03/2017.
 */

public class ParseURL extends AsyncTask<String, Void, String> {
    private static final String TAG = "ParserURL";
    private final static String URL = "http://www.resultados-futbol.com/primera";

    private Context context;
    private ParseUrlListener callback;
    private ProgressDialog progress;
    private ArrayList<String> listaPartidos;


    public ParseURL(Context context, ParseUrlListener callback) {
        this.context = context;
        this.callback = callback;
        listaPartidos = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress = ProgressDialog.show(
                context,
                "Descargando listado de partidos",
                "Espere mientras se descargan los partidos", true);
    }

    @Override
    protected String doInBackground(String... strings) {
        String resultado = "OK";
        Log.d(TAG, "Comprobando entradas de: " + URL);
        // Comprobacion de 200 al hacer la peticion
        if (getStatusConnectionCode(URL) == 200) {
            // Se obtiene el HTML de la web en un objeto Document
            Document document = getHtmlDocument(URL);
            Elements entradas = document.select(".rstd");
            if (entradas != null) {
                for (Element elem : entradas) {
                    String partidos = elem.getElementsByAttribute("title").text().trim();
                    if (partidos != null && !partidos.isEmpty()) {
                        listaPartidos.add(partidos.substring(0, partidos.indexOf("2") - 1));
                    }
                }
            }
        } else {
            Log.e(TAG, "El Status Code no es OK es: " + getStatusConnectionCode(URL));
            resultado = "KO";
        }
        return resultado;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.onParseUrlListener(listaPartidos);
        progress.dismiss();
    }


    /**
     * Con este metodo se comprueba el Status code de la respuesta que se recibe al hacer la peticion
     * EJM:
     * 200 OK			300 Multiple Choices
     * 301 Moved Permanently	305 Use Proxy
     * 400 Bad Request		403 Forbidden
     * 404 Not Found		500 Internal Server Error
     * 502 Bad Gateway		503 Service Unavailable
     *
     * @param url
     * @return Status Code
     */
    public int getStatusConnectionCode(String url) {
        Connection.Response response = null;
        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            Log.e(TAG, "Excepción al obtener el Status Code: " + ex.getMessage());
        }
        if (response != null)
            return response.statusCode();
        else
            return 500;
    }

    /**
     * Con este metodo se devuelve un objeto de la clase Document con el contenido del
     * HTML de la web que permitira parsearlo con los metodos de la librelia JSoup
     *
     * @param url
     * @return Documento con el HTML
     */
    public Document getHtmlDocument(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (IOException ex) {
            Log.e(TAG, "Excepción al obtener el HTML de la página" + ex.getMessage());
        }
        return doc;
    }

    interface ParseUrlListener {
        void onParseUrlListener(ArrayList<String> listaPartidos);
    }
}