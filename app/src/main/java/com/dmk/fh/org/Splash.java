package com.dmk.fh.org;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.realm.SyncUser;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (SyncUser.all().size() > 0) {
                            startActivity(new Intent(Splash.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(Splash.this, LoginActivity.class));
                        }
                        finish();

                    }
                });
            }
        }).start();
    }
}
