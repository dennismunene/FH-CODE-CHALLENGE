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

public class SingUpActivity extends AppCompatActivity {

    private Button signupButton;
    private  TextView link_login;
    private EditText input_email,input_password,input_password_rpt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_password_rpt = findViewById(R.id.input_reEnterPassword);

        link_login = findViewById(R.id.link_login);
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(new Intent(SingUpActivity.this,LoginActivity.class));
            }
        });

        signupButton = findViewById(R.id.btn_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }


    public void signup() {


        if (!validate()) {

            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = input_email.getText().toString();
        final String password = input_password.getText().toString();

        SyncCredentials credentials = SyncCredentials.usernamePassword(email,password,true);
        SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                progressDialog.dismiss();
                signupButton.setEnabled(true);

                Toast.makeText(getBaseContext(), "Successfully created account.", Toast.LENGTH_LONG).show();


                startActivity(new Intent(SingUpActivity.this,LoginActivity.class));
                finish();
            }

            @Override
            public void onError(ObjectServerError error) {

                signupButton.setEnabled(true);
                progressDialog.dismiss();

                Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

                String message = ""+error.getErrorMessage();



                AlertDialog.Builder bd = new AlertDialog.Builder(SingUpActivity.this);
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


    public boolean validate() {
        boolean valid = true;


        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        String reEnterPassword = input_password_rpt.getText().toString();





        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("enter a valid email address");
            valid = false;
        } else {
            input_email.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            input_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            input_password.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            input_password_rpt.setError("Password Do not match");
            valid = false;
        } else {
            input_password_rpt.setError(null);
        }

        return valid;
    }


}
