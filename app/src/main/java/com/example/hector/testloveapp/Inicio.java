package com.example.hector.testloveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import DTO.ContactoDTO;
import DTO.GcmDTO;
import DTO.PreguntasDTO;


public class Inicio extends ActionBarActivity {

    private EditText nombreUsuario;
    private EditText claveUsuario;
    private Button botonLoguear;
    private Button botonRegistrar;
    private ProgressDialog pdialog;
    public static final String TAG = "GCMTestLoveApp";
    private Context context;
    GcmDTO gcmDTO=new GcmDTO();
    Util util=new Util();
    public static GoogleCloudMessaging gcm;
    private String regid="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        nombreUsuario=(EditText) findViewById(R.id.nombreUsuario);
        claveUsuario=(EditText) findViewById(R.id.claveUsuario);
        botonLoguear=(Button) findViewById(R.id.botonLoguear);
        botonRegistrar=(Button) findViewById(R.id.botonRegistrar);

        botonLoguear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context = getApplicationContext();

                TareaLogin tarea=new TareaLogin();
                tarea.execute(nombreUsuario.getText().toString(),claveUsuario.getText().toString());





            }
        });


        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                intent = new Intent(Inicio.this, Registrar.class);
                startActivity(intent);



            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class TareaLogin extends AsyncTask<String,Integer,String>
    {
        ServicioRest servicio=new ServicioRest();
        String resultadoLogin="";
        GcmDTO gcmdto=new GcmDTO();
        ContactoDTO cDTO=new ContactoDTO();
        PreguntasDTO pDTO=new PreguntasDTO();
        int resultCompCache=0;

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            Log.i(TAG, "INI doInBackground");

            try {

                Log.i(TAG, "PASO 1");
                resultCompCache = util.compararGcmCache(context, Inicio.class, params[0]);

                cDTO = servicio.recuperarContactoXUsuario(params[0]);
                pDTO = servicio.getCountPreguntasXUsuario(params[0]);
                util.registrarContactoEnCache(context,cDTO.getContacto());
                util.registrarCantidadPreguntaEnCache(context,pDTO.getCantidadPreguntas());
                Log.i(TAG, "PASO 2");


                if(resultCompCache == Constantes.CACHE_NOT_FOUND_USER || resultCompCache == Constantes.CACHE_NOT_FOUND_REG_ID){
                    //Si no es el mismo usuario se recuperan datos desde el servidor y se validan
                    //Si no es el mismo regId se recuperan datos desde el servidor y se validan
                    resultadoLogin = servicio.loguearUsuario(params[0], params[1]);
                    if(resultadoLogin.equals("1")) {
                        gcmdto = servicio.recuperarGcmXUsuario(params[0]);
                        //cDTO = servicio.recuperarContactoXUsuario(params[0]);
                        //pDTO = servicio.getCountPreguntasXUsuario(params[0]);

                        util.registrarDatosCacheFromServidor(context, params[0], gcmdto.getGcm_codGcm(), gcmdto.getAppVersion(), gcmdto.getExpirationTime());
                        //util.registrarContactoEnCache(context,cDTO.getContacto());
                        //util.registrarCantidadPreguntaEnCache(context,pDTO.getCantidadPreguntas());

                        resultCompCache = util.compararGcmCache(context, Inicio.class, params[0]);

                        if (resultCompCache == Constantes.CACHE_NOT_EXPIRATION_TIME || resultCompCache == Constantes.CACHE_NOT_APP_VERSION) {
                            //Si expiro tiempo se registra nuevamente en google
                            //si version es distinta se registra en google
                            if (gcm == null) {
                                gcm = GoogleCloudMessaging.getInstance(context);
                            }

                            //Nos registramos en los servidores de GCM
                            regid = gcm.register(Constantes.SENDER_ID);
                            util.actualizarDatosCacheFromServidor(context, params[0], regid);

                            /*Agregar servicio para actualizar informacion en servidor*/

                        }
                    }
                }else if(resultCompCache == Constantes.CACHE_NOT_EXPIRATION_TIME || resultCompCache == Constantes.CACHE_NOT_APP_VERSION){
                    //Si expiro tiempo se registra nuevamente en google
                    //si version es distinta se registra en google
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    //Nos registramos en los servidores de GCM
                    regid = gcm.register(Constantes.SENDER_ID);
                    util.actualizarDatosCacheFromServidor(context,params[0],regid);

                     /*Agregar servicio para actualizar informacion en servidor*/



                }else{
                    resultadoLogin = servicio.loguearUsuario(params[0], params[1]);
                }

                Log.i(TAG, "Termino bien,RESULTADO=" + resultadoLogin);
                return msg;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }
            @Override
        protected void onCancelled() {
            Toast.makeText(Inicio.this, "Login cancelado" , Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {


        }

        @Override
        protected void onPreExecute() {


        }


        protected void onPostExecute(String result) {
            Log.d(TAG,"Entro a OnPostExecute");

                if(this.resultadoLogin.equals("1")){
                    Toast.makeText(Inicio.this, "Login Exitoso!",Toast.LENGTH_SHORT).show();

                    Intent intent;
                    intent = new Intent(Inicio.this, MenuPrincipal.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(Inicio.this, "Intentelo nuevamente!",Toast.LENGTH_SHORT).show();
                };


        }

    }




}
