package com.feedback.football.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.feedback.football.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.feedback.football.utils.Utils.getStringJugadaId;
import static com.feedback.football.utils.Utils.getStringMinutoId;

public class MainActivity extends AppCompatActivity implements ParseURL.ParseUrlListener {
    private static final String TAG = "MainActivity";

    private AppCompatSpinner partidos;
    private SpinnerAdapter dataAdapter;
    private ArrayList<String> listaPartidos = new ArrayList<>();
    private AppCompatSpinner minuto;
    private AppCompatSpinner jugada;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        partidos = (AppCompatSpinner) findViewById(R.id.form_partidos_jornada);
        minuto = (AppCompatSpinner) findViewById(R.id.form_minuto);
        jugada = (AppCompatSpinner) findViewById(R.id.form_jugada);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Guardar en firebase y obtener los datos de ahí
        if (listaPartidos != null && listaPartidos.size() == 0) {
            (new ParseURL(this, this/*ParseUrlListener*/)).execute(URL);
        } else {
            dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, listaPartidos);
        }
        partidos.setAdapter(dataAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("partidos", listaPartidos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listaPartidos = savedInstanceState.getStringArrayList("partidos");
    }

    @Override
    public void onParseUrlListener(ArrayList<String> listaPartidos) {
        this.listaPartidos = listaPartidos;
        if (listaPartidos != null && listaPartidos.size() > 0) {
            dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, listaPartidos);
        } else {
            dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Datos no cargados"});
        }
        partidos.setAdapter(dataAdapter);
    }

    /**
     * Funcionalidad al pulsar la votación
     *
     * @param view
     */
    public void votingClickOk(View view) {
        // TODO: Guardar el resultado en Firebase
        dbRef = FirebaseDatabase.getInstance().getReference().child("votacion");
        if (listaPartidos != null && listaPartidos.size() > 0) {
            String id = dbRef.push().getKey();
            // Partido
            dbRef.child(id).child("partido").setValue(listaPartidos.get(partidos.getSelectedItemPosition()));
            // Minuto
            dbRef.child(id).child("minuto").setValue(getStringMinutoId(MainActivity.this, minuto.getSelectedItemPosition()));
            // Jugada
            dbRef.child(id).child("jugada").setValue(getStringJugadaId(MainActivity.this, jugada.getSelectedItemPosition()));
            // Voto ok
            dbRef.child(id).child("ok").setValue(+1);
        }
        Toast.makeText(this, "Se ha guardado su voto", Toast.LENGTH_SHORT).show();
    }

    /**
     * Funcionalidad al pulsar la votación
     *
     * @param mainActivity
     * @param view
     */
    public void votingClickKo(View view) {
        // TODO: Guardar el resultado en Firebase
        dbRef = FirebaseDatabase.getInstance().getReference().child("votacion");
        if (listaPartidos != null && listaPartidos.size() > 0) {
            String id = dbRef.push().getKey();
            // Partido
            dbRef.child(id).child("partido").setValue(listaPartidos.get(partidos.getSelectedItemPosition()));
            // Minuto
            dbRef.child(id).child("minuto").setValue(getStringMinutoId(MainActivity.this, minuto.getSelectedItemPosition()));
            // Jugada
            dbRef.child(id).child("jugada").setValue(getStringJugadaId(MainActivity.this, jugada.getSelectedItemPosition()));
            // Voto ok
            dbRef.child(id).child("ko").setValue(+1);
        }
        Toast.makeText(this, "Se ha guardado su voto", Toast.LENGTH_SHORT).show();
    }

}

//Web Scraping
//https://jarroba.com/scraping-java-jsoup-ejemplos/