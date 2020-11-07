package com.example.gupshup.SignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gupshup.Common.NodesList;
import com.example.gupshup.Login.LoginActivity;
import com.example.gupshup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity
{
    private ImageView profPic;
    private TextInputEditText editName, editEmail, editPassword,  editConfirmPassword;
    private String name,email,password, confirmPassword;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri localFileUri, serverFileUri;
    View progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editName = findViewById(R.id.editNameSignup);
        editEmail = findViewById(R.id.editEmailSignUp);
        editPassword = findViewById(R.id.editPasswordSignUp);
        editConfirmPassword = findViewById(R.id.editCPassword);
        profPic = findViewById(R.id.profPic);

        progBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void PickImage(View view)
    {
        Intent imgPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imgPick,101);
        /*if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }*/
    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 102)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent imgPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imgPick,101);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Access to media is required",Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101)
        {
            if(resultCode == RESULT_OK)
            {
                localFileUri = data.getData();
                profPic.setImageURI(localFileUri);
            }
        }
    }

    private void UpdatePhoto()
    {
        final String serverFileName = firebaseUser.getUid() + ".jpg";
        progBar.setVisibility(View.VISIBLE);

        final StorageReference fileRef = storageReference.child("profilePics/" + serverFileName);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {progBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(final Uri uri)
                        {
                            serverFileUri = uri;

                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        String UserID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodesList.USERS);

                                        HashMap<String,String> userInfo = new HashMap<>();

                                        userInfo.put(NodesList.NAME,editName.getText().toString().trim());
                                        userInfo.put(NodesList.EMAIL,editEmail.getText().toString().trim());
                                        userInfo.put(NodesList.STATUS,"true");
                                        userInfo.put(NodesList.PHOTO,serverFileUri.getPath());

                                        databaseReference.child(UserID).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                Toast.makeText(SignUpActivity.this,"User created successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                            }
                                        });

                                    }
                                    else
                                    {
                                        Toast.makeText(SignUpActivity.this,"Sign-up Failed: "+
                                                task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }
                    });
                }
            }
        });
    }

    public void UpdateUserToDB()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(editName.getText().toString().trim())
                .build();

        progBar.setVisibility(View.VISIBLE);
        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                progBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    String UserID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodesList.USERS);

                    HashMap<String,String> userInfo = new HashMap<>();

                    userInfo.put(NodesList.NAME,editName.getText().toString().trim());
                    userInfo.put(NodesList.EMAIL,editEmail.getText().toString().trim());
                    userInfo.put(NodesList.STATUS,"true");
                    userInfo.put(NodesList.PHOTO,"");

                    progBar.setVisibility(View.VISIBLE);
                    databaseReference.child(UserID).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this,"User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                        }
                    });

                }
                else
                {
                    Toast.makeText(SignUpActivity.this,"Sign-up Failed: "+
                            task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void SignUp(View view)
    {
        name = editName.getText().toString().trim();
        email = editEmail.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        confirmPassword = editConfirmPassword.getText().toString().trim();

        if(name.isEmpty())
        {
            editName.setError("Name field is required!");
        }
        else if(email.isEmpty())
        {
            editEmail.setError("Email field is required!");
        }
        else if(password.isEmpty())
        {
            editPassword.setError("Password field is required!");
        }
        if(confirmPassword.isEmpty())
        {
            editConfirmPassword.setError("Confirm  field is required!");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editEmail.setError("Use correct format for email");
        }
        else if(!(password.equals(confirmPassword)))
        {
            editConfirmPassword.setError("Passwords do not match");
            editPassword.setError("Passwords do not match");
        }
        else
        {
            progBar.setVisibility(View.VISIBLE);
            final FirebaseAuth signUpAuth = FirebaseAuth.getInstance();
            signUpAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progBar.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        firebaseUser = signUpAuth.getCurrentUser();

                        if(localFileUri !=null)
                        {
                            UpdatePhoto();
                        }
                        else
                        {
                            UpdateUserToDB();
                        }
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this,"Sign-up Failed: "+
                                task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}