package com.dmk.fh.org;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.dmk.fh.org.model.Child;
import com.dmk.fh.org.model.Sponsor;
import com.hbb20.CountryCodePicker;

import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.SyncUser;

public class AddSponsorActivity extends AppCompatActivity {


    private AppCompatEditText edName,edAge,edNotes;
    private Spinner spGender;
    private CountryCodePicker ccp;
    private AppCompatButton btnSave;

    private  String gender;
    private String country;

    private Realm realm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addsponsor_activity);


        edName = findViewById(R.id.edName);
        edAge = findViewById(R.id.edAge);
        edNotes = findViewById(R.id.edNotes);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        spGender = findViewById(R.id.spGender);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0)
                    gender = "m";
                else
                    gender = "f";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ccp = findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
               country =  ccp.getSelectedCountryName();
            }
        });





    }

    private void saveData() {

        //validate fields
        String name = edName.getText().toString();
        String age = edAge.getText().toString();

        String notes = edNotes.getText().toString();

        boolean isValid = true;

        if(name.isEmpty()){
            isValid = false;
            edName.setError("Name required!");
        }

        if(age.isEmpty()){
            isValid = false;
            edName.setError("Age required!");
        }


        if(isValid){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving Data...");
            progressDialog.show();


            String userId = SyncUser.current().getIdentity();

                        Sponsor sponsor = new Sponsor();
                        sponsor.setId(new Random().nextInt());
            sponsor.setName(name);
            sponsor.setAge(Integer.parseInt(age));
            sponsor.setGender(gender);
            sponsor.setCountry(country);
            sponsor.setDateCreated(new Date());
            sponsor.setNotes(notes);


           realm =  Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                public void execute(Realm realm) {
                    realm.insert(sponsor);
                }
            });



            Realm.getInstanceAsync(Realm.getDefaultConfiguration(), new Realm.Callback() {
                @Override
                public void onSuccess(Realm realm) {
                    AddSponsorActivity.this.realm =realm;
                    runOnUiThread(() -> {
                                realm.close();
                                progressDialog.dismiss();
                                finish();
                            });

                }
            });
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
