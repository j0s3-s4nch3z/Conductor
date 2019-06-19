package cl.domito.dmttransfer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewDetalleAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.CambiarEstadoServicioOperation;
import cl.domito.dmttransfer.thread.DesAsignarServicioOperation;
import cl.domito.dmttransfer.thread.RealizarServicioOperation;

public class ServicioDetalleActivity extends AppCompatActivity {

    private ImageView imageViewAtrasInt;
    private RecyclerView recyclerViewDetalle;
    private RecyclerView.Adapter mAdapterDetalle;
    private RecyclerView.LayoutManager layoutManagerDetalle;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private TextView textviewServicioValor;
    private TextView textviewFechaValor;
    private TextView textviewClienteValor;
    private TextView textviewRutaValor;
    private TextView textviewTRutaValor;
    private TextView textviewTarifaValor;
    private TextView textviewCantidadValor;
    private TextView textviewObservacionValor;
    private String estado = "";
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_detalle);

        recyclerViewDetalle = (RecyclerView) findViewById(R.id.recyclerViewDetalle);
        recyclerViewDetalle.setHasFixedSize(true);
        layoutManagerDetalle = new LinearLayoutManager(this);
        recyclerViewDetalle.setLayoutManager(layoutManagerDetalle);
        imageViewAtrasInt = findViewById(R.id.imageViewAtrasInt);
        buttonConfirmar = findViewById(R.id.buttonFinalizar);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        textviewServicioValor = findViewById(R.id.textViewIdServicioValor);
        textviewFechaValor = findViewById(R.id.textViewFechaValor);
        textviewClienteValor = findViewById(R.id.textViewClienteValor);
        textviewRutaValor = findViewById(R.id.textViewRutaValor);
        textviewTRutaValor = findViewById(R.id.textViewTRutaValor);
        textviewTarifaValor = findViewById(R.id.textViewTarifaValor);
        textviewCantidadValor = findViewById(R.id.textViewCantidadValor);
        textviewObservacionValor = findViewById(R.id.textViewObservacionValor);

        conductor = Conductor.getInstance();

        conductor.context = ServicioDetalleActivity.this;

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aceptarServicio();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desasignarServicio();
            }
        });

        imageViewAtrasInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        obtenerServicios();
        super.onPostCreate(savedInstanceState);
    }

    private void volver() {
        String activity = getIntent().getExtras().getString("activity");
        if(!activity.equals("cl.domito.conductor.activity.MainActivity"))
        {
            conductor.volver = true;
        }
        this.finish();
    }

    private void aceptarServicio() {
        if (estado.equals("1")) {
            RealizarServicioOperation realizarServicioOperation = new RealizarServicioOperation(this);
            realizarServicioOperation.execute();
        } else if (estado.equals("3")) {
            try {
                TextView textView = findViewById(R.id.textViewFechaValor);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = sdf.parse(textView.getText().toString());
                Long l = date.getTime();
                Date dateNow = new Date();
                Long lNow = dateNow.getTime();
                Long data = Math.abs(lNow - l);
                if (data <= 3.6e+6 || dateNow.after(date)) {
                    if(conductor.servicioActualRuta.contains("RG") || conductor.servicioActualRuta.contains("ESP")) {
                        CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                        cambiarEstadoServicioOperation.execute(conductor.servicioActual,"4","");
                    }
                    Intent mainIntent = new Intent(this, PasajeroActivity.class);
                    startActivity(mainIntent);
                    this.finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Falta mas de 1 hora para el inicio del servicio", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (estado.equals("4")) {
            Intent mainIntent = new Intent(this, PasajeroActivity.class);
            startActivity(mainIntent);
            this.finish();
        }
    }

    private void desasignarServicio() {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Rechazar Servicio");
        dialogo1.setMessage("� Esta seguro que desea rechazar este servicio?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                desasignar();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    // aqui no
                } else {

                }
                return;
            }
        }
    }

    private void llamar(String numero) {
        if (numero.equals("")) {
            Toast.makeText(this, "No se suministro un n�mero de telefono", Toast.LENGTH_LONG);
        } else {
            ActivityUtils.llamar(this, numero);
        }
    }

    private void desasignar() {
        DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation(this);
        desAsignarServicioOperation.execute();
    }

    private void obtenerServicios() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(getIntent().getExtras().getString("fecha"));
            Long l = date.getTime();
            Date dateNow = new Date();
            Long lNow = dateNow.getTime();
            Long data = Math.abs(lNow - l);
            try {
                int cantidad = 0;
                ArrayList<String> lista = new ArrayList();
                if (conductor.servicio != null) {
                    JSONObject primero = conductor.servicio.getJSONObject(0);
                    String ruta = primero.getString("servicio_truta").split("-")[0];
                    if (ruta.equals("ZP")) {
                        String cliente = primero.getString("servicio_cliente");
                        String destino = primero.getString("servicio_cliente_direccion");
                        lista.add(cliente + "%%" + destino);

                    }
                }
                for (int i = 0; i < conductor.servicio.length(); i++) {
                    JSONObject servicio = conductor.servicio.getJSONObject(i);
                    conductor.servicioActual = servicio.getString("servicio_id");
                    textviewServicioValor.setText(servicio.getString("servicio_id"));
                    textviewFechaValor.setText(servicio.getString("servicio_fecha") + " " + servicio.getString("servicio_hora"));
                    textviewClienteValor.setText(servicio.getString("servicio_cliente"));
                    textviewRutaValor.setText(servicio.getString("servicio_ruta"));
                    textviewTRutaValor.setText(servicio.getString("servicio_truta"));
                    textviewTarifaValor.setText(servicio.getString("servicio_tarifa"));
                    textviewObservacionValor.setText(servicio.getString("servicio_observacion").equals("") ? "Sin observaciones" : servicio.getString("servicio_observacion"));
                    cantidad++;
                    estado = servicio.getString("servicio_estado");
                    String nombre = servicio.getString("servicio_pasajero_nombre");
                    String celular = servicio.getString("servicio_pasajero_celular");
                    String destino = servicio.getString("servicio_destino");
                    String cliente = servicio.getString("servicio_cliente_direccion");
                    conductor.servicioActual = servicio.getString("servicio_id");
                    conductor.servicioActualRuta = servicio.getString("servicio_truta");
                    lista.add(nombre + "%" + celular + "%" + destino);
                }
                if (conductor.servicio != null) {
                    JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                    String ruta = ultimo.getString("servicio_truta").split("-")[0];
                    if (ruta.equals("RG")) {
                        String cliente = ultimo.getString("servicio_cliente");
                        String destino = ultimo.getString("servicio_cliente_direccion");
                        lista.add(cliente + "%%" + destino);
                    }
                }
                textviewCantidadValor.setText(cantidad + "");
                conductor.cantidadPasajeros = cantidad;
                if (lista.size() > 0) {
                    String[] array = new String[lista.size()];
                    array = lista.toArray(array);
                    ReciclerViewDetalleAdapter mAdapter = new ReciclerViewDetalleAdapter(this, array);
                    recyclerViewDetalle.setAdapter(mAdapter);
                    //conductor.setOcupado(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (estado.equals("1")) {
                buttonConfirmar.setText("Aceptar");
            } else if (estado.equals("3")) {
                buttonConfirmar.setText("Iniciar");
            } else if (estado.equals("4")) {
                buttonConfirmar.setText("Continuar");
                buttonConfirmar.setWidth(0);
                buttonCancelar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}