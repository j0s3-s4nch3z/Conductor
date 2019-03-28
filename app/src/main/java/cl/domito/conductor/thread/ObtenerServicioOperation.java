package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class ObtenerServicioOperation extends AsyncTask<String, Void, JSONArray> {



    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONArray jsonArray = null;
        Conductor conductor = Conductor.getInstance();
        String idServicio = strings[0];
        String url = Utilidades.URL_BASE_SERVICIO + "GetServicioProgramado.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.getId()));
        params.add(new BasicNameValuePair("id",idServicio));
        try {
            jsonArray = RequestConductor.getServicios(url, params);
        }
        catch(Exception e)
        {e.printStackTrace();}
        return jsonArray;
    }
}
