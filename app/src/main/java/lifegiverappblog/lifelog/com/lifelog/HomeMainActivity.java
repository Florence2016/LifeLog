package lifegiverappblog.lifelog.com.lifelog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lifegiverappblog.lifelog.com.lifelog.posts.LgPostActivity;

public class HomeMainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;
    private FloatingActionButton fab_post_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Lifelog");

        fab_post_add = findViewById(R.id.fab_post);
        fab_post_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent LgPostIntent = new Intent(HomeMainActivity.this, LgPostActivity.class);
                startActivity(LgPostIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
           sendToLogin();
        }
        else
            {
                current_user_id = mAuth.getCurrentUser().getUid();
                firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                         if(task.isSuccessful())
                         {
                                if(!task.getResult().exists())
                                {
                                    Intent setupIntent = new Intent(HomeMainActivity.this, SetupAccountActivity.class);
                                    startActivity(setupIntent);
                                    finish();
                                }
                         }
                         else
                             {
                                 String errorMessage = task.getException().getMessage();
                                 Toast.makeText(HomeMainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG);
                             }
                    }
                });
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:
                Intent accSettingsIntent = new Intent(HomeMainActivity.this, SetupAccountActivity.class);
                startActivity(accSettingsIntent);

                default:
                    return false;
        }

    }
    private void sendToLogin() {
        Intent intent = new Intent(HomeMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOut() {
       mAuth.signOut();
       sendToLogin();
    }

}