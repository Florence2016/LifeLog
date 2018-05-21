package lifegiverappblog.lifelog.com.lifelog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {


    private EditText reg_etEmail, reg_etPass, reg_etConfirmPass;
    private Button reg_btn, reg_close_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_etEmail = (EditText)findViewById(R.id.reg_email);
        reg_etPass = (EditText)findViewById(R.id.reg_password);
        reg_etConfirmPass = (EditText)findViewById(R.id.reg_confirm_pass);

        reg_btn = (Button)findViewById(R.id.btn_register);
        reg_close_btn = (Button)findViewById(R.id.btn_close_reg);

        reg_progress = (ProgressBar)findViewById(R.id.register_progress);

        reg_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = reg_etEmail.getText().toString();
                String password = reg_etPass.getText().toString();
                String confirmpass = reg_etConfirmPass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpass)){
                    if(password.equals(confirmpass)){

                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                            Intent setupAccIntent = new Intent(RegisterActivity.this, SetupAccountActivity.class);
                            startActivity(setupAccIntent);
                            finish();

                            }
                            else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error: "+ errorMessage, Toast.LENGTH_LONG).show();
                            }
                            reg_progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            sendToHomeMain();
        }
    }

    private void sendToHomeMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, HomeMainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
