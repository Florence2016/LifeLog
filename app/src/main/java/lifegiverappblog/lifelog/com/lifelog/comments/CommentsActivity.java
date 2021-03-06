package lifegiverappblog.lifelog.com.lifelog.comments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lifegiverappblog.lifelog.com.lifelog.HomeMainActivity;
import lifegiverappblog.lifelog.com.lifelog.R;
import lifegiverappblog.lifelog.com.lifelog.adapters.CommentsRecyclerAdapter;
import lifegiverappblog.lifelog.com.lifelog.models.Comments;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;

    private String lg_post_id, current_user_id, comment_post_image;
    private EditText comment_field;

    private ImageView commentImageCommentViewer;
    private FloatingActionButton comment_post_btn;
    private RecyclerView comment_list;

    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        current_user_id = firebaseAuth.getCurrentUser().getUid();
        lg_post_id = getIntent().getStringExtra("lg_post_id");
        comment_post_image = getIntent().getStringExtra("comment_post_image");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        Query firstQuery = firebaseFirestore.collection("Posts/" + lg_post_id + "/Comments").orderBy("timestamp", Query.Direction.ASCENDING);
        firstQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String comment_message = comment_field.getText().toString();
                if(!TextUtils.isEmpty(comment_message)){

                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_message);
                commentsMap.put("user_id", current_user_id);
                commentsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/" + lg_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(CommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            comment_field.setText("");
                            Toast.makeText(CommentsActivity.this, "Comment Sent", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(CommentsActivity.this, HomeMainActivity.class));
                            finish();
                        }
                    }
                });
                }
                else
                    {
                        Toast.makeText(CommentsActivity.this, "Fill up the field to comment", Toast.LENGTH_LONG).show();
                }
            }
        });
        commentImageCommentViewer = findViewById(R.id.comment_imageView);
        Glide.with(this).load(comment_post_image).into(commentImageCommentViewer);

    }


}
