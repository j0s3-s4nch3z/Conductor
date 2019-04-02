package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.ObtenerProduccionOperation;

public class ProduccionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageViewAtras;
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produccion);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProduccion);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        conductor = Conductor.getInstance();

        conductor.context = ProduccionActivity.this;

        ObtenerProduccionOperation obtenerProduccionOperation = new ObtenerProduccionOperation(this);
        obtenerProduccionOperation.execute();

        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });
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
