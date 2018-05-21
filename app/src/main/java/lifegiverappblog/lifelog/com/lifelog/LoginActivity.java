package lifegiverappblog.lifelog.com.lifelog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin,btnRegister;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etLoginEmail = (EditText)findViewById(R.id.reg_email);
        etLoginPassword = (EditText)findViewById(R.id.reg_password);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnRegister = (Button)findViewById(R.id.btn_login_reg);

        mProgressBar = (ProgressBar)findViewById(R.id.login_progress);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String loginEmail = etLoginEmail.getText().toString();
            String loginPass = etLoginPassword.getText().toString();

            if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                mProgressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sendToHomeMain();
                        }
                        else{
                            String errorMessage = task. getException().getMessage();
                            Toast.makeText(LoginActivity.this, "Error:"+ errorMessage, Toast.LENGTH_LONG).show();
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToHomeMain();
        }
    }

    private void sendToHomeMain() {
        Intent mainIntent = new Intent(LoginActivity.this, HomeMainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
