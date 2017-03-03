package com.feedback.football.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.feedback.football.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String URL = "http://www.resultados-futbol.com/primera";
    //    public final static int MAX_PAGES = 5;
    private static final String TAG = "MainActivity";
    ProgressDialog progress;
    private AppCompatSpinner partidos;
    private TextInputLayout minuto;
    private FloatingActionButton okButton;
    private FloatingActionButton koButton;
    private ArrayList<String> listaPartidos = new ArrayList<>();
    private SpinnerAdapter dataAdapter;
    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                partidos.setAdapter(dataAdapter);
            }
            return false;
        }
    };
    private View.OnKeyListener spinnerOnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                partidos.setAdapter(dataAdapter);
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        partidos = (AppCompatSpinner) findViewById(R.id.form_partidos_jornada);
        minuto = (TextInputLayout) findViewById(R.id.form_minuto);
        okButton = (FloatingActionButton) findViewById(R.id.form_ok);
        koButton = (FloatingActionButton) findViewById(R.id.form_ko);

        okButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.checkmark));
        koButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.delete));

        partidos.setOnTouchListener(spinnerOnTouch);

    }

    public void votingClick(View view) {
        Toast.makeText(this, "Se ha guardado su voto", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new ParseURL()).execute(URL);
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

    private class ParseURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = ProgressDialog.show(
                    MainActivity.this,
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
                        String partidos = elem.getElementsByAttribute("title").text();
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

            if (listaPartidos != null && listaPartidos.size() > 0)
                dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, listaPartidos);
            else
                dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Sin conexión"});

            progress.dismiss();
        }
    }
}
