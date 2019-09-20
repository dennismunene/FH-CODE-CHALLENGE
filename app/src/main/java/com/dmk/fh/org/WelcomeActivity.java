package com.dmk.fh.org;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;


public class WelcomeActivity extends AppCompatActivity {

    private EditText nicknameView;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (SyncUser.current() != null) {
            gotoListActivity();
        }

        // Set up the login form.
        nicknameView = findViewById(R.id.nickname);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> attemptLogin());
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        // Reset errors.
        nicknameView.setError(null);
        // Store values at the time of the login attempt.
        String nickname = nicknameView.getText().toString();
        showProgress(true);


        SyncCredentials credentials = SyncCredentials.nickname(nickname, false);
        SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                showProgress(false);
                gotoListActivity();
            }

            @Override
            public void onError(ObjectServerError error) {
                showProgress(false);
                nicknameView.setError("Uh oh something went wrong! (check your logcat please)");
                nicknameView.requestFocus();
                Log.e("Login error", error.toString());
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void gotoListActivity() {
        Intent intent = new Intent(WelcomeActivity.this,
                MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "You Go Man! Thats the Stuff", Toast.LENGTH_SHORT).show();
    }
}