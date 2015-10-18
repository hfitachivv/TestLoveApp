package com.example.hector.testloveapp;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import DTO.ContactoDTO;
import DTO.GcmDTO;
import DTO.PreguntaDTO;
import DTO.PreguntasDTO;
import Util.JsonParseador;

/**
 * Created by hector on 20-06-2015.
 */
public class ServicioRest {

    boolean resul = true;
    public static final String TAG = "GCMTestLoveApp";


    public String registrarUsuario(String nom_user,String password,String cod_gcm,int appVersion,long expirationTime){
        String respStr="";
        String datoObtenido="";
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/usuario/registrarWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();



        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);
            dato.put("password", password);
            dato.put("codGcm", cod_gcm);
            dato.put("expirationTime",expirationTime );
            dato.put("appVersion",appVersion );

            entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            respStr = EntityUtils.toString(resp.getEntity());

            JsonParseador jParser=new JsonParseador();


            Log.i(TAG, "Resp registrarUser="+jParser.getUnDatoSimpleString(respStr,"respuesta"));

            datoObtenido=jParser.getUnDatoSimpleString(respStr,"respuesta");



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datoObtenido;
    }

    public String registrarContacto(String nom_user,String contacto){
        String respStr="";
        String datoObtenido="";
        int httpStatus=0;
        HttpClient httpClient = new DefaultHttpClient();

        HttpPut put =
                new HttpPut("http://testlovebackend-hvillarroel.rhcloud.com/rest/contacto/registrarWithJson");

        put.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();



        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);
            dato.put("contacto", contacto);

            entity = new StringEntity(dato.toString());
            put.setEntity(entity);

            HttpResponse resp = httpClient.execute(put);

            httpStatus=resp.getStatusLine().getStatusCode();

            if(httpStatus==200){

                datoObtenido="1";

            }else if(httpStatus==500){
                datoObtenido="0";
            }





        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datoObtenido;
    }

    public String loguearUsuario(String nom_user,String password){
        String respStr="";
        String datoObtenido="";
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/usuario/loguearWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();





        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);
            dato.put("password", password);

            entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            respStr = EntityUtils.toString(resp.getEntity());

            JsonParseador jParser=new JsonParseador();

            datoObtenido=jParser.getUnDatoSimpleString(respStr,"respuesta");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datoObtenido;
    }

    public GcmDTO recuperarGcmXUsuario(String nom_user){
        String respStr="";
        GcmDTO gcmdto=new GcmDTO();
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/gcm/getGcmXUsuarioWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();



        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);

            entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            respStr = EntityUtils.toString(resp.getEntity());

            JsonParseador jParser=new JsonParseador();
            Log.i(TAG, "Resp recuperarGcmXUsuario ="+respStr);



            gcmdto=jParser.getGcmFromJson(respStr);



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gcmdto;
    }


    public ContactoDTO recuperarContactoXUsuario(String nom_user){
        String respStr="";
        int httpStatus=0;
        ContactoDTO contactoDTO=new ContactoDTO();
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/contacto/getContactoXUsuarioWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();



        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);

            entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);

            httpStatus=resp.getStatusLine().getStatusCode();

            if(httpStatus==200){

                respStr = EntityUtils.toString(resp.getEntity());

                JsonParseador jParser=new JsonParseador();
                Log.i(TAG, "Resp recuperarContactoXUsuario ="+respStr);


                contactoDTO.setContacto(jParser.getUnDatoSimpleString(respStr,"contacto"));

            }else{
                contactoDTO.setContacto("");

            }







        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contactoDTO;
    }


    public PreguntasDTO getCountPreguntasXUsuario(String nom_user){
        String respStr="";
        int httpStatus=0;
        PreguntasDTO pDTO=new PreguntasDTO();
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/pregunta/getCountPreguntasXUsuarioWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();



        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);

            entity = new StringEntity(dato.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);

            httpStatus=resp.getStatusLine().getStatusCode();

            if(httpStatus==200){

                respStr = EntityUtils.toString(resp.getEntity());

                JsonParseador jParser=new JsonParseador();
                Log.i(TAG, "Resp getCountPreguntasXUsuario ="+respStr);


                pDTO.setCantidadPreguntas(Integer.parseInt(jParser.getUnDatoSimpleString(respStr, "cantidadPreguntas")));

            }else if(httpStatus==204){
                pDTO.setCantidadPreguntas(0);

            }







        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pDTO;
    }


    public String preguntar(String nom_user,String pregunta){
        Log.d(TAG, "********preguntar********");

        String respStr="";
        String datoObtenido="";
        int httpStatus=0;
        HttpClient httpClient = new DefaultHttpClient();
        PreguntaDTO pDTO=new PreguntaDTO();
        PreguntasDTO psDTO=new PreguntasDTO();


        HttpPost post =
                new HttpPost("http://testlovebackend-hvillarroel.rhcloud.com/rest/pregunta/preguntarWithJson");

        post.setHeader("content-type", "application/json");

        //Construimos el objeto cliente en formato JSON
        JSONObject dato = new JSONObject();
        JSONArray arrayPregunta1=new JSONArray();

        JSONObject pregunta1 = new JSONObject();
        try {
            pregunta1.put("pregunta",pregunta);
            pregunta1.put("numero","0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayPregunta1.put(pregunta1);


        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);
            dato.put("cantidadPreguntas", "0");
            dato.put("preguntas",arrayPregunta1);


            entity = new StringEntity(dato.toString());
            Log.d(TAG, "entity "+entity.toString());
            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);

            httpStatus=resp.getStatusLine().getStatusCode();

            if(httpStatus==200){

                datoObtenido="1";

            }else{
                datoObtenido="0";
            }





        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datoObtenido;
    }

    public String registrarPregunta(String nom_user,String pregunta){
        String respStr="";
        String datoObtenido="";
        int httpStatus=0;
        HttpClient httpClient = new DefaultHttpClient();
        PreguntaDTO pDTO=new PreguntaDTO();
        PreguntasDTO psDTO=new PreguntasDTO();
        ArrayList<PreguntaDTO>lista=new ArrayList();

        HttpPut put =
                new HttpPut("http://testlovebackend-hvillarroel.rhcloud.com/rest/pregunta/registrarWithJson");

        put.setHeader("content-type", "application/json");

        //Construimos el JSON
        JSONObject dato = new JSONObject();
        JSONArray arrayPregunta1=new JSONArray();



        JSONObject pregunta1 = new JSONObject();
        try {
            pregunta1.put("pregunta",pregunta);
            pregunta1.put("numero","0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayPregunta1.put(pregunta1);

        StringEntity entity;
        try {

            dato.put("nom_user", nom_user);
            dato.put("cantidadPreguntas", "0");
            dato.put("preguntas",arrayPregunta1);


            entity = new StringEntity(dato.toString());
            Log.d(TAG, "entity "+entity.toString());
            put.setEntity(entity);

            HttpResponse resp = httpClient.execute(put);

            httpStatus=resp.getStatusLine().getStatusCode();

            if(httpStatus==200){

                datoObtenido="1";

            }else{
                datoObtenido="0";
            }





        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datoObtenido;
    }

}
