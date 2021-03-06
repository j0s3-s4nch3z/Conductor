package cl.domito.dmttransfer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.EnviarLogOperation;
import cl.domito.dmttransfer.thread.IniciarServicioOperation;
import cl.domito.dmttransfer.thread.ObtenerServicioOperation;

public class PasajeroActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imageView;
    RecyclerView recyclerView;
    Conductor conductor;
    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.cambiarColorBarra(this);
        setContentView(R.layout.activity_pasajero);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = this.findViewById(R.id.recyclerViewPasajero);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        imageView = this.findViewById(R.id.imageView3);
        txtError = this.findViewById(R.id.textViewError);

        conductor = Conductor.getInstance();

        conductor.context = PasajeroActivity.this;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        IniciarServicioOperation iniciarServicioOperation = new IniciarServicioOperation(this);
        iniciarServicioOperation.execute();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        if(conductor.navegando)
        {
            recargarPasajeros();
            conductor.navegando = false;
        }
        super.onResume();
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                recargarPasajeros();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void recargarPasajeros()
    {
        Conductor conductor = Conductor.getInstance();
        ArrayList<String> lista = new ArrayList();
        String idServicio = conductor.servicioActual;
        try {
            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
            conductor.servicio = obtenerServicioOperation.execute(conductor.servicioActual).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        if(conductor.servicio != null) {
            try {
                JSONObject primero = conductor.servicio.getJSONObject(0);
                String ruta = primero.getString("servicio_truta").split("-")[0];
                if (!conductor.zarpeIniciado && ruta.equals("ZP")) {
                    String cliente = primero.getString("servicio_cliente");
                    String destino = primero.getString("servicio_cliente_direccion");
                    StringBuilder builder = StringBuilderUtil.getInstance();
                    builder.append(cliente).append("%%").append(destino).append("%0%0");
                    lista.add(builder.toString() );
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
            }
        }
        if(conductor.servicio != null) {
            for (int i = 0; i < conductor.servicio.length(); i++) {
                try {
                    JSONObject servicio = conductor.servicio.getJSONObject(i);
                    if (servicio.getString("servicio_id").equals(idServicio)) {
                        String id = servicio.getString("servicio_pasajero_id");
                        String nombre = servicio.getString("servicio_pasajero_nombre");
                        String celular = servicio.getString("servicio_pasajero_celular");
                        String destino = servicio.getString("servicio_destino");
                        String estado = servicio.getString("servicio_pasajero_estado");
                        StringBuilder builder = StringBuilderUtil.getInstance();
                        if(servicio.getString("servicio_truta").contains("ZP")) {
                            if (!estado.equals("3") && !estado.equals("2")) {
                                builder.append(nombre).append("%").append(celular).append("%")
                                        .append(destino).append("%").append(estado).append("%").append(id);
                                lista.add(builder.toString());
                            }
                        }
                        else if(servicio.getString("servicio_truta").contains("RG")) {
                            if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                builder.append(nombre).append("%").append(celular).append("%")
                                        .append(destino).append("%").append(estado).append("%").append(id);
                                lista.add(builder.toString());
                            }
                        } else if(servicio.getString("servicio_truta").contains("XX")) {
                            if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                if(!destino.equals("")) {
                                    builder.append(nombre).append("%").append(celular).append("%")
                                            .append(destino).append("%").append(estado).append("%").append(id);
                                    lista.add(builder.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                    enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
                }
            }
        }
        if(conductor.servicio != null) {
            try {
                JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                String ruta = ultimo.getString("servicio_truta").split("-")[0];
                if (ruta.equals("RG")) {
                    String cliente = ultimo.getString("servicio_cliente");
                    String destino = ultimo.getString("servicio_cliente_direccion");
                    StringBuilder builder = StringBuilderUtil.getInstance();
                    builder.append(cliente).append("%%").append(destino).append("%0%0");
                    lista.add(builder.toString());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array  = lista.toArray(array);
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(this,array);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(this,null);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        conductor.volver = true;
        super.onBackPressed();
    }

    private void volver()
    {
        conductor.volver = true;
        this.finish();
    }

}
