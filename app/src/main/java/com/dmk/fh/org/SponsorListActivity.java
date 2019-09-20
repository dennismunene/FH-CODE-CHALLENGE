package com.dmk.fh.org;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dmk.fh.org.adapter.ChildListAdapter;
import com.dmk.fh.org.adapter.MyDividerItemDecoration;
import com.dmk.fh.org.adapter.SponsorListAdapter;
import com.dmk.fh.org.model.Child;
import com.dmk.fh.org.model.Sponsor;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SponsorListActivity extends AppCompatActivity {
    private RecyclerView rcChildren;
    private AppCompatButton btnAddNew;
    private Realm realm;

    private SponsorListAdapter mAdapter;

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childlist);

        rcChildren = findViewById(R.id.rcChildren);
        rcChildren.setLayoutManager(new LinearLayoutManager(this));
        rcChildren.setItemAnimator(new DefaultItemAnimator());
        rcChildren.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));


        btnAddNew = findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SponsorListActivity.this,AddSponsorActivity.class));
            }
        });




        Realm.getInstanceAsync(Realm.getDefaultConfiguration(), new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                SponsorListActivity.this.realm = realm;
                RealmResults<Sponsor> sponsors =  realm.where(Sponsor.class).sort("dateCreated", Sort.ASCENDING).findAllAsync();


                mAdapter = new SponsorListAdapter(sponsors);
                rcChildren.setAdapter(mAdapter);

            }
        });


        //delete option
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AlertDialog.Builder bd = new AlertDialog.Builder(viewHolder.itemView.getContext());
                bd .setTitle("Delete!");
                bd.setMessage("Are you sure you want to delete this?");
                bd .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int position = viewHolder.getAdapterPosition();

                        int itemID = mAdapter.getItem(position).getId();
                        realm.executeTransactionAsync(realm -> {
                            Sponsor item = realm.where(Sponsor.class).equalTo("id", itemID).findFirst();
                            if (item != null) {
                                item.deleteFromRealm();
                            }
                        });
                    }});
                bd.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                bd.create().show();


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcChildren);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
