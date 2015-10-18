package Util;

import org.json.JSONException;
import org.json.JSONObject;

import DTO.GcmDTO;

/**
 * Created by hector on 26-09-2015.
 */
public class JsonParseador {

    JSONObject mainObject =null;
    JSONObject uniObject=null;

    public String getUnDatoSimpleString(String jsonString,String datoObtener){

        String dato="";
        try {
            this.mainObject = new JSONObject(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            dato=mainObject.getString(datoObtener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(dato.equals("null")){
            dato="";
        }
    return dato;
    }

    public Object getObjecto(Class clase){
        return null;
    }

    public GcmDTO getGcmFromJson(String jsonString){

       GcmDTO gcmdto=new GcmDTO();
        String Gcm_codGcm="";
        long expirationTime=0;
        int appVersion=0;

        try {
            this.mainObject = new JSONObject(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*try {
            this.uniObject = this.mainObject.getJSONObject("Gcm");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        try {
            Gcm_codGcm=this.mainObject.getString("gcm_codGcm");
            expirationTime=this.mainObject.getLong("expirationTime");
            appVersion=this.mainObject.getInt("appVersion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gcmdto.setGcm_codGcm(Gcm_codGcm);
        gcmdto.setExpirationTime(expirationTime);
        gcmdto.setAppVersion(appVersion);

    return gcmdto;

    }

}
