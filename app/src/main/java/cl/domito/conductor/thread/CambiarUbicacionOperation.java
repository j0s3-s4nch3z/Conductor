package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class CambiarUbicacionOperation extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;

    public CambiarUbicacionOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_MOVIL + "ModUbicacionMovil.php";
        RequestConductor.actualizarUbicacion(url,conductor.getLocation());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Conductor conductor = Conductor.getInstance();
        TextView textViewTemporal = context.get().findViewById(R.id.textViewTemporal);
        textViewTemporal.setText("LAT: "+conductor.getLatitud() + " LON: "+conductor.getLongitud());

    }
}
