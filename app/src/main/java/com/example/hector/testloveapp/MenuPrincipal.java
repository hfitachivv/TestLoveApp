package com.example.hector.testloveapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;

import DTO.PreguntaDTO;


public class MenuPrincipal extends ActionBarActivity {

    private EditText labelAgregarContacto;
    private EditText labelPregunta;
    private Button buttonAgregarContacto;
    private Button buttonGuardarPregunta;
    private Button buttonPreguntar;
    private Spinner spinnerPreguntas;
    private String contacto="";
    int numeroPreguntas=0;

    ArrayList<String>preguntas=new ArrayList<String>();
    public static final String TAG = "GCMTestLoveApp";
    Util util=new Util();
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        context = getApplicationContext();


        labelAgregarContacto=(EditText) findViewById(R.id.LabelAgregarContacto);
        buttonAgregarContacto=(Button) findViewById(R.id.ButtonAgregarContacto);
        buttonGuardarPregunta=(Button) findViewById(R.id.ButtonGuardarPregunta);
        buttonPreguntar=(Button) findViewById(R.id.ButtonPreguntar);
        labelPregunta=(EditText) findViewById(R.id.LabelPregunta);
        spinnerPreguntas=(Spinner) findViewById(R.id.SpinnerPreguntas);
        contacto=util.getContactoCache(context);

        if(contacto.equals("contactoDefaultTestLove")|| contacto.equals("")){
            buttonAgregarContacto.setVisibility(Button.VISIBLE);
            labelAgregarContacto.setVisibility(Button.VISIBLE);
            labelPregunta.setVisibility(Button.GONE);
            buttonGuardarPregunta.setVisibility(Button.GONE);
            buttonPreguntar.setVisibility(Button.GONE);
            spinnerPreguntas.setVisibility(Spinner.GONE);
        }else{
            buttonAgregarContacto.setVisibility(Button.GONE);
            labelAgregarContacto.setVisibility(Button.GONE);
            buttonGuardarPregunta.setVisibility(Button.VISIBLE);
            labelPregunta.setVisibility(Button.VISIBLE);
            buttonPreguntar.setVisibility(Button.GONE);
            spinnerPreguntas.setVisibility(Spinner.GONE);
        }




        buttonAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TareaRegistroContacto tarea=new TareaRegistroContacto();
                tarea.execute(util.getUserCache(context),labelAgregarContacto.getText().toString());

            }
        });


        buttonGuardarPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TareaGuardarPregunta tarea=new TareaGuardarPregunta();
                tarea.execute(util.getUserCache(context),labelPregunta.getText().toString());

            }
        });

        buttonPreguntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numeroPreguntas=util.getCantidadPreguntaEnCache(context);
                spinnerPreguntas.setVisibility(Spinner.VISIBLE);

                for(int i=0;i<numeroPreguntas;i++){

                    PreguntaDTO pDTO=new PreguntaDTO();

                    pDTO.setPregunta(util.getPreguntaCacheByNumArray(context,i+1));
                    if(!pDTO.getPregunta().equals("preguntaDefaultTestLove")){
                        preguntas.add(pDTO.getPregunta());
                    }

                }

                ArrayAdapter<String>adaptador=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,preguntas);
                adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPreguntas.setAdapter(adaptador);

                spinnerPreguntas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        TareaPregunta tareaP=new TareaPregunta();

                        tareaP.execute(util.getUserCache(context),parent.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_principal, menu);
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

    private class TareaRegistroContacto extends AsyncTask<String,Integer,String>
    {
        ServicioRest servicio=new ServicioRest();
        String resulTarea="0";

        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";

            //Nos registramos en nuestro servidor
            resulTarea= servicio.registrarContacto(params[0],params[1]);
            util.registrarContactoEnCache(context,params[1]);

            return msg;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "Entro a OnPostExecute");

            if (this.resulTarea.equals("1")) {
                Toast.makeText(MenuPrincipal.this, "Registro de contacto exitoso!", Toast.LENGTH_SHORT).show();
                buttonAgregarContacto.setVisibility(Button.GONE);
                labelAgregarContacto.setVisibility(Button.GONE);
                buttonGuardarPregunta.setVisibility(Button.VISIBLE);
                labelPregunta.setVisibility(Button.VISIBLE);
                buttonPreguntar.setVisibility(Button.GONE);
                spinnerPreguntas.setVisibility(Spinner.GONE);
            } else {
                Toast.makeText(MenuPrincipal.this, "No se ha registrado el contacto", Toast.LENGTH_SHORT).show();
            }

        }
    }



    private class TareaGuardarPregunta extends AsyncTask<String,Integer,String>
    {
        ServicioRest servicio=new ServicioRest();
        String resulTarea="0";

        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";


           resulTarea= servicio.registrarPregunta(params[0],params[1]);

            util.registrarPreguntaEnCache(context, params[1]);
            util.actualizarCantidadPreguntaEnCache(context);

            return msg;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "Entro a OnPostExecute");

            if (this.resulTarea.equals("1")) {
                Toast.makeText(MenuPrincipal.this, "Registro de pregunta exitoso!", Toast.LENGTH_SHORT).show();
                buttonAgregarContacto.setVisibility(Button.GONE);
                labelAgregarContacto.setVisibility(Button.GONE);
                buttonGuardarPregunta.setVisibility(Button.GONE);
                labelPregunta.setVisibility(Button.GONE);
                buttonPreguntar.setVisibility(Button.VISIBLE);
                spinnerPreguntas.setVisibility(Spinner.VISIBLE);
            } else {
                Toast.makeText(MenuPrincipal.this, "No se ha registrado la pregunta", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class TareaPregunta extends AsyncTask<String,Integer,String>
    {
        ServicioRest servicio=new ServicioRest();
        String resulTarea="0";

        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";


            resulTarea= servicio.preguntar(params[0],params[1]);


            return msg;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "Entro a OnPostExecute");

            if (this.resulTarea.equals("1")) {
                Toast.makeText(MenuPrincipal.this, "Ya se envio tu pregunta", Toast.LENGTH_SHORT).show();



            } else {
                Toast.makeText(MenuPrincipal.this, "No se ha enviado tu pregunta", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
