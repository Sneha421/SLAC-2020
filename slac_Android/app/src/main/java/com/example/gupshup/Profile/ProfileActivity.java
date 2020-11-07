package com.example.gupshup.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.Login.LoginActivity;
import com.example.gupshup.Password.ChangePasswordActivity;
import com.example.gupshup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity
{
    private ImageView profPic;
    EditText editName, editEmail;
    TextView chngPwd;
    String name,email;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri localFileUri, serverFileUri;
    private FirebaseAuth firebaseAuth;
    Button save;
    ImageView image;
    View progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_profile);

        editName = findViewById(R.id.editNameProfile);
        editEmail = findViewById(R.id.editEmailProfile);
        profPic = findViewById(R.id.profPic);

        progBar = findViewById(R.id.progressBar);

        chngPwd = findViewById(R.id.chngPwd);
        chngPwd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        image = findViewById(R.id.profPic);
        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(serverFileUri==null)
                {
                    PickImage();
                }
                else
                {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_pic,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem)
                        {
                            int menuID = menuItem.getItemId();

                            switch (menuID)
                            {
                                case R.id.changePic:
                                    PickImage();
                                    break;
                                case R.id.removePic:
                                    RemovePhoto();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            }
        });


        save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                name = editName.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                if(name.isEmpty())
                {
                    editName.setError("Enter name");
                }
                else
                {
                    if(localFileUri!=null)
                    {
                        UpdatePhoto();
                    }
                    else
                    {
                        UpdateUserToDB();
                    }
                }
            }
        });


        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null)
        {
            editName.setText(firebaseUser.getDisplayName());
            editEmail.setText(firebaseUser.getEmail());

            serverFileUri = firebaseUser.getPhotoUrl();

            if(serverFileUri!=null)
            {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(profPic);
            }
        }


    }



    public void RemovePhoto()
    {

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(editName.getText().toString().trim())
                .setPhotoUri(null)
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
                    userInfo.put(NodesList.PHOTO,"");


                    databaseReference.child(UserID).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            finish();
                        }
                    });

                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Sign-up Failed: "+
                            task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void LogOut(View view)
    {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child(NodesList.TOKENS).child(firebaseUser.getUid());


        databaseReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Something went wrong : "+task.getException()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void ChangeImage(View view)
    {

    }

    public void PickImage()
    {
        Intent imgPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imgPick,101);
    }



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

        final StorageReference fileRef = storageReference.child("profilePics/" + serverFileName);

        progBar.setVisibility(View.VISIBLE);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                progBar.setVisibility(View.GONE);

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
                                                finish();
                                            }
                                        });

                                    }
                                    else
                                    {
                                        Toast.makeText(ProfileActivity.this,"Sign-up Failed: "+
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
                    userInfo.put(NodesList.PHOTO,serverFileUri.getPath());


                    databaseReference.child(UserID).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(ProfileActivity.this,"User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                        }
                    });

                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Sign-up Failed: "+
                            task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}