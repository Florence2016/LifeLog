package lifegiverappblog.lifelog.com.lifelog.posts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import lifegiverappblog.lifelog.com.lifelog.R;

public class LgPostActivity extends AppCompatActivity {

    private Toolbar toolbar_lg_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lg_post);

        toolbar_lg_post = findViewById(R.id.toolbar_addpost_lg);
        setSupportActionBar(toolbar_lg_post);
        getSupportActionBar().setTitle("Lifegroup Log");
    }
}
