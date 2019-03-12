package cl.domito.conductor.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class NotificationOperation extends AsyncTask<String, Void, String[]> {

    private Context context;
    private Button buttonConfirmar;
    private TextView textViewEstadoValor;

    public NotificationOperation(Context context) {
        this.context = context;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String[] respuesta = new String[2];
        Conductor conductor = Conductor.getInstance();
        JSONArray jsonArray = RequestConductor.obtenerNotificaciones();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("notificacion_id");
                String tipo = jsonObject.getString("notificacion_tipo");
                respuesta[0] = id;
                respuesta[1] = tipo;
                if(tipo.equals("0")) {
                    ActivityUtils.enviarNotificacion(context, "", jsonObject.getString("notificacion_texto"), R.drawable.furgoneta, ServicioActivity.class);
                }
                else if(tipo.equals("1"))
                {
                    String fecha = jsonObject.getString("notificacion_fecha");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = sdf.parse(fecha);
                    Date dateNow = new Date();
                    if(Math.abs(date.getTime() - dateNow.getTime()) < 1.8e+6) {
                        ActivityUtils.enviarNotificacion(context, "", jsonObject.getString("notificacion_texto"), R.drawable.furgoneta,ServicioActivity.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return respuesta;
    }

    @Override
    protected void onPostExecute(String[] aString) {
        //if(aString[1].equals("0")) {
            CambiarEstadoNotificacionOperation cambiarEstadoNotificacionOperation = new CambiarEstadoNotificacionOperation();
            cambiarEstadoNotificacionOperation.execute(aString[0]);
        //}
    }

}