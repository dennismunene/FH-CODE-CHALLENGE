package com.dmk.fh.org;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class LoginActivity extends AppCompatActivity {


    private Button btnLogin;
    private TextView tvRegister,tvForgotPassword;
    private EditText edEmail,edPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPass);




        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
               // startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SingUpActivity.class));
            }
        });

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });
    }

    private void login() {

        //validate input first
        boolean isValid = true;

        String email = edEmail.getText().toString();

        String password = edPassword.getText().toString();

        if(email.isEmpty()){
            isValid = false;
            edEmail.setError("Email required!");
        }

        if(password.isEmpty()){
            edPassword.setError("Password required!");
            isValid = false;
        }


        if(isValid) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging In...");
            progressDialog.show();

            SyncCredentials credentials = SyncCredentials.usernamePassword(email, password, false);
            SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                @Override
                public void onSuccess(SyncUser user) {
                    progressDialog.dismiss();


                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(ObjectServerError error) {


                    progressDialog.dismiss();

                    Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

                    String message = "" + error.getErrorMessage();


                    AlertDialog.Builder bd = new AlertDialog.Builder(LoginActivity.this);
                    bd.setTitle("Alert");
                    bd.setMessage(message);
                    bd.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    bd.show();


                    Log.e("Login error", error.toString());
                }
            });

        }

    }

}
