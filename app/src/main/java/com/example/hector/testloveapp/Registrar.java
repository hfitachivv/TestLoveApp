package com.example.hector.testloveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import DTO.GcmDTO;


public class Registrar extends ActionBarActivity {

    private EditText nombreUsuarioRegistro;
    private EditText claveUsuarioRegistro;
    private Button botonRegistrarRegistro;
    private Context context;
    public static GoogleCloudMessaging gcm;
    public static final String TAG = "GCMTestLoveApp";
    private String regid="";
    private String respRegisUser="";
    boolean registrado=false;
    GcmDTO gDTO=new GcmDTO();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        nombreUsuarioRegistro=(EditText) findViewById(R.id.nombreUsuarioRegistro);
        claveUsuarioRegistro=(EditText) findViewById(R.id.claveUsuarioRegistro);
        botonRegistrarRegistro=(Button) findViewById(R.id.botonRegistrarRegistro);



        botonRegistrarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context = getApplicationContext();

                //Chequemos si está instalado Google Play Services
                if(checkPlayServices())
                {
                gcm = GoogleCloudMessaging.getInstance(Registrar.this);

                    //Obtenemos el Registration ID guardado
                   regid = getRegistrationId(context);

                //Si no disponemos de Registration ID comenzamos el registro
                if (regid.equals("")) {
                    TareaRegistroGCM tarea = new TareaRegistroGCM();
                   // Toast.makeText(Registrar.this, "Registrando en nuestros servidres", Toast.LENGTH_SHORT).show();

                    tarea.execute(nombreUsuarioRegistro.getText().toString(),claveUsuarioRegistro.getText().toString());
                }else{
                    Toast.makeText(Registrar.this, "Datos ya se encuentran en nuestros servidres", Toast.LENGTH_SHORT).show();

                    Intent intent;
                    intent = new Intent(Registrar.this, Inicio.class);
                    startActivity(intent);
                 }
                }
               else
                {
                   Log.i(TAG, "No se ha encontrado Google Play Services.");
                }



            }
        });


    }


    @Override
    protected void onResume()
    {
        super.onResume();

      checkPlayServices();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrar, menu);
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


    private class TareaRegistroGCM extends AsyncTask<String,Integer,String>
    {
        ServicioRest servicio=new ServicioRest();
        int appVersion=0;
        String resulTarea="0";
        String resulVerificacion="";
        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";

            Log.d(TAG, "Entro a doInBackground");


            resulVerificacion=servicio.loguearUsuario(params[0],params[1]);
            if(resulVerificacion.equals("0")){

                if (gcm == null)
                {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    Log.d(TAG, "PASO 1");
                }

                //Nos registramos en los servidores de GCM
                try {


                    regid = gcm.register(Constantes.SENDER_ID);
                    Log.d(TAG, "PASO 2");
                    appVersion = getAppVersion(context);
                    Log.d(TAG, "PASO 3");


                    resulTarea=servicio.registrarUsuario(params[0],params[1], regid,appVersion,System.currentTimeMillis() + Constantes.EXPIRATION_TIME_MS);
                    if(resulTarea.equals("1")){
                        registrado=true;
                    }


                    //Guardamos los datos del registro
                    if(registrado)
                    {
                        setRegistrationId(context, params[0], regid);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            Log.d(TAG, "Registrado en GCM: registration_id=" + regid);

            //Nos registramos en nuestro servidor
            Log.d(TAG, "PASO 4 "+msg);


            return msg;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG,"Entro a OnPostExecute");

            if(this.resulTarea.equals("1")){
                Toast.makeText(Registrar.this, "Registro Exitoso!",Toast.LENGTH_SHORT).show();

                Intent intent;
                intent = new Intent(Registrar.this, Inicio.class);
                startActivity(intent);

            }else{
                Toast.makeText(Registrar.this, "No te has podido registrado",Toast.LENGTH_SHORT).show();
            };


        }
    }

    private String getRegistrationId(Context context)
    {

        SharedPreferences prefs = getSharedPreferences(
                Registrar.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(Constantes.PROPERTY_REG_ID, "");

        if (registrationId.length() == 0)
        {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(Constantes.PROPERTY_USER, "user");

        int registeredVersion =
                prefs.getInt(Constantes.PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(Constantes.PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        }
        else if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        }
        else if (!nombreUsuarioRegistro.getText().toString().equals(registeredUser))
        {
            Log.d(TAG, "Nuevo nombre de usuario.");
            return "";
        }

        return registrationId;
    }

    private void setRegistrationId(Context context, String user, String regId)
    {
        SharedPreferences prefs = getSharedPreferences(
                "TestLoveApp",
                Context.MODE_PRIVATE);

        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.PROPERTY_USER, user);
        editor.putString(Constantes.PROPERTY_REG_ID, regId);
        editor.putInt(Constantes.PROPERTY_APP_VERSION, appVersion);
        editor.putLong(Constantes.PROPERTY_EXPIRATION_TIME,
                System.currentTimeMillis() + Constantes.EXPIRATION_TIME_MS);

        editor.commit();
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,Registrar.this,Constantes.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "Dispositivo no soportado.");
                finish();
            }
            return false;
        }
        return true;
    }

}
