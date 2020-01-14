package cl.domito.dmttransfer.thread;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.List;

import cl.domito.dmttransfer.activity.PasajeroActivity;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.service.BurbujaService;

public class NavegarOperation extends AsyncTask<String, Void, Void> {

    WeakReference<PasajeroActivity> context;
    Conductor conductor;

    public NavegarOperation(PasajeroActivity activity) {
        context = new WeakReference<PasajeroActivity>(activity);
        conductor = Conductor.getInstance();
    }
    @Override
    protected Void doInBackground(String... strings) {
        String destino = strings[0];
        context.get().startService(new Intent(context.get(), BurbujaService.class));
        conductor.navegando = true;
        try {
            //Geocoder geocoder = new Geocoder(context.get());
            Location location = new Location("");
            /*List<Address> addresses = null;
            boolean error = true;
            int i = 0;
            do {
                try {

                    addresses = geocoder.getFromLocationName(destino, 1);
                    location.setLatitude(addresses.get(0).getLatitude());
                    location.setLongitude(addresses.get(0).getLongitude());
                    error = false;
                }
                catch(Exception e){
                    i++;
                    if(i == 3){
                        error = false;
                    }
                    else {
                        error = true;
                    }
                }
            }
            while(error);*/
            String response;
            try {
                response = ActivityUtils.getLatLongByURL("https://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(destino,"UTF-8") +"&key=AIzaSyDcQylEsZAzuEw3EHBdWbsDAynXvU2Ljzs");
                JSONObject jsonObject = new JSONObject(response);
                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
                location.setLatitude(lat);
                location.setLongitude(lng);
            } catch (Exception e) {
                e.printStackTrace();
            }
            conductor.locationDestino = location;
            try {
                String uri = null;
                SharedPreferences pref = context.get().getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                String tipoNav = pref.getString("nav", "");
                String paquete = "";
                StringBuilder builder = StringBuilderUtil.getInstance();
                if(tipoNav.equals("google"))
                {
                    /*builder.append("google.navigation:q=").append(addresses.get(0).getLatitude())
                            .append(",").append(addresses.get(0).getLongitude());*/
                    builder.append("google.navigation:q=").append(location.getLatitude())
                            .append(",").append(location.getLongitude());
                    uri = builder.toString() ;
                    paquete = "com.google.android.apps.maps";
                }
                else if(tipoNav.equals("") || tipoNav.equals("waze"))
                {
                    /*builder.append("geo:").append(addresses.get(0).getLatitude())
                            .append(",").append(addresses.get(0).getLongitude());*/
                    builder.append("geo:").append(location.getLatitude())
                            .append(",").append(location.getLongitude());
                    uri = builder.toString();
                    paquete = "com.waze";
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage(paquete);
                context.get().startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                context.get().startActivity(intent);
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,ex.getMessage(),ex.getClass().getName(),ex.getStackTrace()[0].getLineNumber()+"");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return null;
    }
}
