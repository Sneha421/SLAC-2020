package com.example.gupshup.Password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gupshup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity
{
    EditText editEmailResetPwd;
    TextView resetPwdTextView;
    LinearLayout linLayoutResetPwd, linLayoutMsg;
    Button resetPwd, retry, close;
    View progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.gupshup.R.layout.activity_reset_password);

        editEmailResetPwd = findViewById(R.id.editEmailResetPwd);
        resetPwdTextView = findViewById(R.id.resetInstMsg);
        linLayoutResetPwd = findViewById(R.id.linLayoutResetPwd);
        linLayoutMsg = findViewById(R.id.linLayoutMsg);
        resetPwd = findViewById(R.id.resetPwdButton);
        retry = findViewById(R.id.retryButton);
        close = findViewById(R.id.closeButton);

        progBar = findViewById(R.id.progressBar);
        resetPwd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String email = editEmailResetPwd.getText().toString().trim();
                if(email.isEmpty())
                {
                    System.out.println("Enter email");

                }
                else
                {
                    progBar.setVisibility(View.VISIBLE);
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            progBar.setVisibility(View.GONE);
                            linLayoutResetPwd.setVisibility(View.GONE);
                            linLayoutMsg.setVisibility(View.VISIBLE);

                            if(task.isSuccessful())
                            {
                                resetPwdTextView.setText(getString(R.string.resert_password_instructions, email));
                                new CountDownTimer(60000, 1000)
                                {
                                    @Override
                                    public void onTick(long l) {
                                        retry.setText(getString(R.string.resend_timer, String.valueOf(l/1000)));
                                        retry.setOnClickListener(null);
                                    }

                                    @Override
                                    public void onFinish() {
                                        retry.setText(R.string.retry_btn);

                                        retry.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                linLayoutResetPwd.setVisibility(View.VISIBLE);
                                                linLayoutMsg.setVisibility(View.GONE);
                                            }
                                        });

                                    }
                                }.start();
                            }
                            else
                            {
                                resetPwdTextView.setText(getString(R.string.email_sent_failed, email));
                                retry.setText(R.string.retry_btn);

                                retry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        linLayoutResetPwd.setVisibility(View.VISIBLE);
                                        linLayoutMsg.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });

        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }
}