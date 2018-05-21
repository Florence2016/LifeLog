package lifegiverappblog.lifelog.com.lifelog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupAccountActivity extends AppCompatActivity {

    private CircleImageView setupImageAcc;
    private Uri mainImageURI = null;
    private EditText setupNameAcc;
    private Button setupBtn;
    private ProgressBar setupProgressbar;
    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);

        Toolbar setupAccountToolbar = findViewById(R.id.setupAccToolbar);
        setSupportActionBar(setupAccountToolbar);
        getSupportActionBar().setTitle("Account Setup");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupNameAcc = findViewById(R.id.setup_account_name);
        setupImageAcc = findViewById(R.id.setup_account_image);
        setupBtn = findViewById(R.id.setup_account_save_btn);
        setupProgressbar= findViewById(R.id.setup_account_progressbar);

        setupProgressbar.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);
        //to retrieve data
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);
                        setupNameAcc.setText(name);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_avatar);
                        Glide.with(SetupAccountActivity.this).load(image).into(setupImageAcc);
                    }
                }
                else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(SetupAccountActivity.this, "FireStore Retrieve Error: " + error, Toast.LENGTH_LONG).show();
                    }
                setupProgressbar.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String username = setupNameAcc.getText().toString();

                if(!TextUtils.isEmpty(username) && mainImageURI != null)
                {
                    setupProgressbar.setVisibility(View.VISIBLE);
                    if(isChanged)
                    {
                            user_id = firebaseAuth.getCurrentUser().getUid();
                            StorageReference image_path = storageReference.child("profile_image").child( user_id + ".jpg");
                            image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                                {

                                    if(task.isSuccessful())
                                    {
                                        //method to store data on the firestore
                                        storeToFirestore(task,username);
                                    }
                                    else
                                        {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SetupAccountActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                        setupProgressbar.setVisibility(View.INVISIBLE);
                                        }
                                }
                            });
                    }
                        else
                            {
                                storeToFirestore(null,username);
                            }
                }
            }
        });
        setupImageAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //if runs in Marshmallow version, permission is required from the user
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    //if permission not yet granted, ask user for the permission
                    if(ContextCompat.checkSelfPermission(SetupAccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(SetupAccountActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupAccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else
                        {
                        imageSelectorCropper();
                        }
                }
                else
                    {
                    imageSelectorCropper();
                    }
            }
        });
    }

    private void storeToFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username)
    {
        Uri download_uri;
        if(task !=null)
        {
            download_uri = task.getResult().getDownloadUrl();
        }
        else
            {
                download_uri = mainImageURI;
            }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("image", download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(SetupAccountActivity.this, "User Account Settings Updated", Toast.LENGTH_LONG).show();

                    Intent homeMainIntent = new Intent(SetupAccountActivity.this, HomeMainActivity.class);
                    startActivity(homeMainIntent);
                    finish();
                }
                else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(SetupAccountActivity.this, "Server Error: " + error, Toast.LENGTH_LONG).show();
                    }

                setupProgressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void imageSelectorCropper()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupAccountActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {

                mainImageURI = result.getUri();
                setupImageAcc.setImageURI(mainImageURI);
                isChanged =true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
