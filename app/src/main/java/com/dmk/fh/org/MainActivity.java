package com.dmk.fh.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;

import io.realm.SyncUser;

public class MainActivity extends AppCompatActivity {


    private CardView cardChildren,cardSponsors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cardChildren = findViewById(R.id.card_children);
        cardChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ChildListActivity.class));
            }
        });
        cardSponsors = findViewById(R.id.card_sponsors);
        cardSponsors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SponsorListActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Map<String, SyncUser> all = SyncUser.all();

            for (Map.Entry<String, SyncUser> entry : all.entrySet()) {
                entry.getValue().logOut();
            }


                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
