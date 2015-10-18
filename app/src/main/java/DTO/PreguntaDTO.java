package DTO;

/**
 * Created by hector on 11-10-2015.
 */
public class PreguntaDTO {

    private String pregunta;
    private long numero;


    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Pregunta{" +
                "pregunta=" + pregunta +
                "}";
    }
}
