package cl.domito.conductor.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.LoginOperation;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private CheckBox checkBoxRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();
        mUserView = findViewById(R.id.usuario);
        mPasswordView = findViewById(R.id.password);
        mEmailSignInButton = findViewById(R.id.login_button);
        checkBoxRec = findViewById(R.id.checkBox);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginConductor();
            }
        });
        checkBoxRec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recordarInicioSesion();
            }
        });
    }

    private void loginConductor() {
        String usuario = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!usuario.equals("") && !password.equals(""))
        {
            LoginOperation loginOperation = new LoginOperation(this);
            loginOperation.execute(usuario,password);
            ActivityUtils.hideSoftKeyBoard(this);
        }
        else
        {
            Toast t = Toast.makeText(this, "Ingrese tanto usuario como password", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    private void recordarInicioSesion() {
        Conductor.getInstance().setRecordarSession(true);
    }



}

