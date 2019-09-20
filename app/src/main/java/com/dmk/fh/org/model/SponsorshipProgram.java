package com.dmk.fh.org.model;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

//Spornsorship Programs
public class SponsorshipProgram extends RealmObject {

    @PrimaryKey

    private int id;
    @Required
    private String name;


    private int amount;

    //period in days
    private int period;

    private int childID;


    private int sponsorID;

    @Required
    private Date dateStarted;

    private Date dateEnded;





}
