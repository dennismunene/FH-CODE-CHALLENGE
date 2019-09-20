package com.dmk.fh.org;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.dmk.fh.org.adapter.SponsorAutoCompleteAdapter;
import com.dmk.fh.org.adapter.SponsorListAdapter;
import com.dmk.fh.org.model.Child;
import com.dmk.fh.org.model.Sponsor;
import com.dmk.fh.org.model.SponsorshipProgram;
import com.hbb20.CountryCodePicker;

import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncUser;

public class AddChildActivity extends AppCompatActivity {


    private static final int CFCT_ID = 1001;
    private AppCompatEditText edName, edAge, edNotes;
    private Spinner spGender;
    private CountryCodePicker ccp;
    private AppCompatButton btnSave;
    private AppCompatAutoCompleteTextView edAutoComplete;

    private String gender;
    private String country,countryNameCode;


    private Realm realm;
    Child childExtra;

    private Sponsor selectedSponsor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addchild_activity);


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

        edAutoComplete = findViewById(R.id.edAutoComplete);
        edAutoComplete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSponsor = (Sponsor) edAutoComplete.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spGender = findViewById(R.id.spGender);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0)
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
                country = ccp.getSelectedCountryName();
                countryNameCode = ccp.getDefaultCountryCode();


            }
        });

        Realm.getInstanceAsync(Realm.getDefaultConfiguration(), new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {

                RealmResults<Sponsor> sponsors =  realm.where(Sponsor.class).sort("dateCreated", Sort.ASCENDING).findAllAsync();


                edAutoComplete.setAdapter(new SponsorAutoCompleteAdapter(sponsors,AddChildActivity.this));


            }
        });

         childExtra = (Child) getIntent().getParcelableExtra("child");

        if (childExtra != null) {
            edName.setText(""+childExtra.getName());
            edAge.setText(""+childExtra.getAge());
            edNotes.setText(""+childExtra.getNotes());

            country = childExtra.getCountry();
            gender = childExtra.getGender();

            if(gender.equals("m"))
                spGender.setSelection(0);
            else
                spGender.setSelection(1);


            ccp.setDefaultCountryUsingNameCode(childExtra.getCountryNameCode());

        }


    }

    private void saveData() {

        //validate fields
        String name = edName.getText().toString();
        String age = edAge.getText().toString();

        String notes = edNotes.getText().toString();

        boolean isValid = true;

        if (name.isEmpty()) {
            isValid = false;
            edName.setError("Name required!");
        }

        if (age.isEmpty()) {
            isValid = false;
            edName.setError("Age required!");
        }


        if (isValid) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving Data...");
            progressDialog.show();

            String path = "/~/child";
            String url = Constants.REALM_URL + path;

            String userId = SyncUser.current().getIdentity();

            int childID= new Random().nextInt();
            Child child = new Child();
            child.setId(childID);

            child.setName(name);
            child.setAge(Integer.parseInt(age));
            child.setGender(gender);
            child.setCountry(country);
            child.setCountryNameCode(countryNameCode);
            child.setDateCreated(new Date());
            child.setCreatedBy(userId);
            child.setNotes(notes);
            if(selectedSponsor!=null)
            child.setCurrentSponsor(selectedSponsor.getId());


            SponsorshipProgram program = new SponsorshipProgram();
            program.setId(CFCT_ID);
            program.setAmount(38);
            program.setPeriod(30);
            program.setChildID(childID);

            if(selectedSponsor!=null)
            program.setSponsorID(selectedSponsor.getId());

            program.setDateStarted(new Date());



            realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                public void execute(Realm realm) {
                    if(childExtra!=null){

                        //we are updating data
                        realm.insertOrUpdate(child);

                        if(selectedSponsor!=null){
                            realm.insertOrUpdate(program);
                        }
                    }else {
                        realm.insert(child);

                        if(selectedSponsor!=null){
                            realm.insert(program);
                        }
                    }



                }
            });


            Realm.getInstanceAsync(Realm.getDefaultConfiguration(), new Realm.Callback() {
                @Override
                public void onSuccess(Realm realm) {
                    AddChildActivity.this.realm = realm;
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
        if(realm!=null)
        realm.close();
    }

}
