package com.example.hector.testloveapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import DTO.GcmDTO;

/**
 * Created by hector on 20-06-2015.
 */
public class Util {

    public static final String TAG = "GCMTestLoveApp";


    public int compararGcmCache(Context context,Class clase,String nom_user)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);


        String registeredUser = prefs.getString(Constantes.PROPERTY_USER, "userDefaultTestLove");
        if (!nom_user.equals(registeredUser))
        {
            Log.d(TAG, "Distinto usuario.");
            //obtener datos de de servidor
            return Constantes.CACHE_NOT_FOUND_USER;
        }


        String registrationId = prefs.getString(Constantes.PROPERTY_REG_ID, "");

        if (registrationId.equals(""))
        {
            Log.d(TAG, "No tiene regId");
            //obtener datos de de servidor
            return Constantes.CACHE_NOT_FOUND_REG_ID;
        }

        long expirationTime = prefs.getLong(Constantes.PROPERTY_EXPIRATION_TIME, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));
        if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "Registro GCM expirado.");
            return Constantes.CACHE_NOT_EXPIRATION_TIME;
        }


        int registeredVersion = prefs.getInt(Constantes.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return Constantes.CACHE_NOT_APP_VERSION;
        }



        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");


        return Constantes.SUCCESS;
    }

    public static int getAppVersion(Context context)
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

    public void registrarDatosCacheFromServidor(Context context,String user, String regId,int appVersion,long expirationTime)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);

        //int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.PROPERTY_USER, user);
        editor.putString(Constantes.PROPERTY_REG_ID, regId);
        editor.putInt(Constantes.PROPERTY_APP_VERSION, appVersion);
        editor.putLong(Constantes.PROPERTY_EXPIRATION_TIME,expirationTime);

        editor.commit();
    }

    public void actualizarDatosCacheFromServidor(Context context,String user, String regId)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
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

    public String getUserCache(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);


        String registeredUser = prefs.getString(Constantes.PROPERTY_USER, "userDefaultTestLove");
       return registeredUser;
    }

    public String getContactoCache(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);


        String registeredContacto = prefs.getString(Constantes.PROPERTY_CONTACTO, "contactoDefaultTestLove");
        return registeredContacto;
    }

    public void registrarContactoEnCache(Context context,String contacto)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.PROPERTY_CONTACTO,contacto );


        editor.commit();
    }

    public void registrarPreguntaEnCache(Context context,String pregunta)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);
        int cantidadPreguntas=getCantidadPreguntaEnCache(context);

        if(cantidadPreguntas<0 || cantidadPreguntas==0){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constantes.PROPERTY_PREGUNTA + "1",pregunta );
            editor.commit();
        }else if(cantidadPreguntas>0){

            String numArray=Integer.toString(cantidadPreguntas+1);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constantes.PROPERTY_PREGUNTA + numArray,pregunta );
            editor.commit();

        }




    }



    public String getPreguntaCacheByNumArray(Context context,int numArray)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);

        String numArrayString=Integer.toString(numArray);
        String registeredPregunta = prefs.getString(Constantes.PROPERTY_PREGUNTA + numArrayString, "preguntaDefaultTestLove");
        return registeredPregunta;
    }

    public void registrarCantidadPreguntaEnCache(Context context,int cantidadPreguntas)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constantes.PROPERTY_CANTIDAD_PREGUNTA,cantidadPreguntas );


        editor.commit();
    }

    public int getCantidadPreguntaEnCache(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);

        int cantidadPreguntas = prefs.getInt(Constantes.PROPERTY_CANTIDAD_PREGUNTA,-1);
        return cantidadPreguntas;
    }


    public void actualizarCantidadPreguntaEnCache(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constantes.APP_NAME,
                Context.MODE_PRIVATE);

        int cantidadPreguntas=getCantidadPreguntaEnCache(context);
        int nuevaCantidad=cantidadPreguntas+1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constantes.PROPERTY_CANTIDAD_PREGUNTA,nuevaCantidad );


        editor.commit();
    }

/*
    private static boolean checkPlayServices(Class clase) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(clase);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, clase,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
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

*/

}
