package com.feedback.football.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.feedback.football.R;
import com.feedback.football.model.ProductBean;
import com.feedback.football.remote.APIService;
import com.feedback.football.utils.ApiUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.feedback.football.utils.Utils.getStringJugadaId;
import static com.feedback.football.utils.Utils.getStringMinutoId;

public class MainActivity extends AppCompatActivity implements ParseURL.ParseUrlListener {
    private static final String TAG = "MainActivity";
    List<ProductBean> dataList = new ArrayList<>();
    ProductBean productBeanResponse = new ProductBean();
    private AppCompatSpinner partidos;
    private SpinnerAdapter dataAdapter;
    private ArrayList<String> listaPartidos = new ArrayList<>();
    private AppCompatSpinner minuto;
    private AppCompatSpinner jugada;
    private DatabaseReference dbRef;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        partidos = (AppCompatSpinner) findViewById(R.id.form_partidos_jornada);
        minuto = (AppCompatSpinner) findViewById(R.id.form_minuto);
        jugada = (AppCompatSpinner) findViewById(R.id.form_jugada);

        apiService = ApiUtils.getAPIService();

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

        sendPost("Android", "Test Post Android", 10.9);
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

        sendGetAll();

    }

    private void sendGetAll() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://boot-heroku.herokuapp.com/v1/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//      apiService = retrofit.create(APIService.class);
        Call<List<ProductBean>> call = apiService.getData();

        call.enqueue(new Callback<List<ProductBean>>() {
            @Override
            public void onResponse(Call<List<ProductBean>> call, Response<List<ProductBean>> response) {
                switch (response.code()) {
                    case 200:
                        dataList = response.body();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<ProductBean>> call, Throwable t) {
                Log.e(TAG, "Unable to send Get to API.");
            }
        });
        if (dataList.size() > 0) {
            Toast.makeText(this, dataList.get(0).getDescription(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendPost(String name, String description, Double price) {
        ProductBean productBean = new ProductBean(name, description, price);
        apiService.savePost(productBean).enqueue(new Callback<ProductBean>() {
            @Override
            public void onResponse(Call<ProductBean> call, Response<ProductBean> response) {
                if (response.isSuccessful()) {
                    productBeanResponse = response.body();
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ProductBean> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
        if (productBeanResponse.getDescription() != null) {
            Toast.makeText(this, productBeanResponse.getDescription(), Toast.LENGTH_LONG).show();
        }
    }

}
// REST
// http://www.journaldev.com/13639/retrofit-android-example-tutorial
// https://stackoverflow.com/questions/37166267/better-approach-to-call-rest-api-from-android
// https://howtoprogram.xyz/2016/10/17/java-rest-client-example-retrofit-2/

//Web Scraping
//https://jarroba.com/scraping-java-jsoup-ejemplos/