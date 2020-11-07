package com.example.gupshup.Password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.gupshup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText editPassword, editChangePassword;
    String password, changePassword;
    Button changePwd;
    View progBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editPassword = findViewById(R.id.editPasswordCP);
        editChangePassword = findViewById(R.id.editChangePassword);
        progBar = findViewById(R.id.progressBar);

        changePwd = findViewById(R.id.changePwdButton);
        changePwd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                password = editPassword.getText().toString().trim();
                changePassword =editChangePassword.getText().toString().trim();

                if(password.isEmpty())
                {
                    System.out.println("Enter Password");
                }
                else if(changePassword.isEmpty())
                {
                    System.out.println("Enter password again");
                }
                else if(!changePassword.equals(password))
                {
                    System.out.println("Passwords don't match");
                }
                else
                {
                    progBar.setVisibility(View.VISIBLE);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    if(firebaseUser!=null)
                    {
                        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                progBar.setVisibility(View.GONE);
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Password updated successfully",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Password updation failed:" +
                                            task.getException(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}