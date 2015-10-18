package DTO;

import java.util.ArrayList;

/**
 * Created by hector on 11-10-2015.
 */
public class PreguntasDTO {

    private ArrayList<PreguntaDTO> preguntaDTOs;
    private int cantidadPreguntas;


    public ArrayList<PreguntaDTO> getPreguntaDTOs() {
        return preguntaDTOs;
    }
    public void setPreguntaDTOs(ArrayList<PreguntaDTO> preguntaDTOs) {
        this.preguntaDTOs = preguntaDTOs;
    }
    public int getCantidadPreguntas() {
        return cantidadPreguntas;
    }
    public void setCantidadPreguntas(int cantidadPreguntas) {
        this.cantidadPreguntas = cantidadPreguntas;
    }

    @Override
    public String toString() {
        String json="";
        ArrayList<PreguntaDTO> preguntaDTO=new ArrayList();

        preguntaDTO=getPreguntaDTOs();

        for(int i=0;i<preguntaDTOs.size();i++){
            PreguntaDTO pdto=new PreguntaDTO();
            pdto=preguntaDTOs.get(i);
            json=json+"{"+"\""+"pregunta"+"\""+":"+"\""+pdto.getPregunta()+"\"";
            json=json+",";
            json=json+"\""+"numero"+"\""+":"+"\""+pdto.getNumero()+"\""+"}";
            if(i+1<preguntaDTOs.size()){
                json=json+",";
            }
        }

        return  "["+json+"]";
    }
}
